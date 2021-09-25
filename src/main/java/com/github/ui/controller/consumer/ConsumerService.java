package com.github.ui.controller.consumer;

import com.github.client.model.Topic;
import com.github.client.service.ProducerTwitterService;
import org.springframework.stereotype.Service;

import com.github.client.service.ConsumerTwitterService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class ConsumerService {
    private final ConsumerTwitterService consumer;
    private final ProducerTwitterService producer;

    public Flux<Topic> runConsumer(final ConsumerModel model) {
        return consumer.run(model);
    }

    public void runProducer() {
        producer.run();
    }

    public void stopConsumer() {
        producer.stop();
    }
}
