package com.github.client.service;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

@Component
public class StreamService {
    private static JsonParser jsonParser = new JsonParser();

    public void run() {
        Properties properties = new Properties();

        properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "demo-kafka-stream");
        properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());

        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> inputTopic = streamsBuilder.stream("twitter_tweets");

        KStream<String, String> filteredStream = inputTopic.filter((k, jsonTweet) -> extractIdFromTweet(jsonTweet) > 10000);

        filteredStream.to("important_tweets");

        final KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), properties);

        kafkaStreams.start();
    }

    private static int extractIdFromTweet(String value) {
        try {
            return jsonParser.parse(value).getAsJsonObject().get("user").getAsJsonObject().get("followers_count").getAsInt();
        } catch (Exception ex) {
            return 0;
        }
    }
}
