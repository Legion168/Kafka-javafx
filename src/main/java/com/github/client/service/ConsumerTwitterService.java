package com.github.client.service;

import static com.github.common.Constant.BOOTSTRAP_SERVER;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Service;

import com.github.ui.controller.consumer.model.Topic;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

@Slf4j
@Service
public class ConsumerTwitterService {
	public Flux<Topic> run(final Topic topic) {
		log.info("****** [Consumer {} STARTED]", topic.getTopicName());

		final ReceiverOptions<String, String> options = receiverOptions()
				.subscription(Collections.singleton(topic.getTopicName()))
				.addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
				.addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));

		final Flux<ReceiverRecord<String, String>> messages = Flux.defer(() -> {
			final KafkaReceiver<String, String> receiver = KafkaReceiver.create(options);
			return receiver.receive();
		});

		return messages
				.doOnNext(m -> Platform.runLater(() -> topic.getCount().setValue(topic.getCount().getValue() + 1)))
				.map(msg -> topic);
	}

	private ReceiverOptions<String, String> receiverOptions() {
		final String groupId = "kafka-demo";

		final Properties props = new Properties();
		props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
		props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(GROUP_ID_CONFIG, groupId);
		props.put(MAX_POLL_RECORDS_CONFIG, 50);

		return ReceiverOptions.create(props);
	}
}
