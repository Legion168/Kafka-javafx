package com.github.client.service;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Component
public class AdminService {
    public void createTopic() {
        try(Admin admin = createAdmin()) {

            int partitions = 1;
            short replicationFactor = 1;
            String topicName = "test";

            final NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

            CreateTopicsResult result = admin.createTopics(
                    Collections.singleton(newTopic)
            );

            KafkaFuture<Void> future = result.values().get(topicName);
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Admin createAdmin() {
        final Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");

        return Admin.create(properties);
    }
}
