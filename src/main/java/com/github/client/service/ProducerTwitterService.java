package com.github.client.service;

import static com.github.common.Constant.BOOTSTRAP_SERVER;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonParser;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import com.github.client.configuration.ClientTwitter;
import com.twitter.hbc.core.Client;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

@Slf4j
@Service
public class ProducerTwitterService {
	private final ClientTwitter twitterClient;
	private final BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(1000);
	private static JsonParser jsonParser = new JsonParser();
	private Disposable disposable;
	private Client client;

	public void run() {
		log.info("****** [Producer STARTED]");

		client = twitterClient.createClient(msgQueue);

		client.connect();

		final KafkaSender<String, String> sender = createKafkaProducer();

		final Flux<SenderRecord<String, String, Integer>> flux = Flux
				.range(1, 10)
				.flatMap(this::createProducerRecord)
				.doOnError(ex -> {
					ex.printStackTrace();
					stop();
				});

		disposable = sender.send(flux).subscribe();
	}

	public void stop() {
		Try.run(() -> {
			client.stop();
			disposable.dispose();
		}).onSuccess(e -> log.info("****** [Producer CLOSED]")).getOrNull();
	}

	private Mono<SenderRecord<String, String, Integer>> createProducerRecord(final Integer count) {
		return Mono
				.just(client)
				.map(c -> Try.of(() -> msgQueue.poll(5, TimeUnit.SECONDS)).getOrElse(""))
				.filter(s -> !s.isEmpty())
				.map(msg -> {
					final String key = isBitcoin(msg) ? "bitcoin" : "sport";
					return SenderRecord.create(new ProducerRecord<>("twitter_tweets", key, msg), count);
				});
	}

	private KafkaSender<String, String> createKafkaProducer() {
		final Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
		properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

		properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
		properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
		properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));

		final SenderOptions<String, String> senderOptions = SenderOptions.create(properties);

		return KafkaSender.create(senderOptions);
	}

	public ProducerTwitterService(final ClientTwitter twitterClient) {
		this.twitterClient = twitterClient;
	}

	private Boolean isBitcoin(final String value) {
		return jsonParser.parse(value).getAsJsonObject().get("text").getAsString().toLowerCase().contains("#bitcoin");
	}
}
