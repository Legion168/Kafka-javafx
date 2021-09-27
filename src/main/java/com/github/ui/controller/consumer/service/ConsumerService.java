package com.github.ui.controller.consumer.service;

import org.springframework.stereotype.Service;

import com.github.client.service.ConsumerTwitterService;
import com.github.client.service.ProducerTwitterService;
import com.github.ui.controller.consumer.model.Topic;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class ConsumerService {
	private final ConsumerTwitterService consumer;
	private final ProducerTwitterService producer;

	public Flux<Topic> runConsumer(final Topic topic) {
		return consumer.run(topic);
	}

	public void startProducer() {
		producer.run();
	}

	public void stopProducer() {
		producer.stop();
	}
}
