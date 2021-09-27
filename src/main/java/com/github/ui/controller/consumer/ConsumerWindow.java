package com.github.ui.controller.consumer;

import static com.github.common.Constant.TOPIC_BITCOIN;
import static com.github.common.Constant.TOPIC_SPORT;
import static com.github.ui.controller.consumer.model.StatusProducer.STARTED;
import static com.github.ui.controller.consumer.model.StatusProducer.STOPPED;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.github.ui.controller.consumer.model.ConsumerModel;
import com.github.ui.controller.consumer.model.StatusProducer;
import com.github.ui.controller.consumer.model.TableSubscriber;
import com.github.ui.controller.consumer.model.Topic;
import com.github.ui.controller.consumer.service.ConsumerService;
import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;

@Slf4j
@FxmlView
@Component
public class ConsumerWindow implements Initializable {
	private final ConsumerService service;
	private final ConsumerModel model;

	@FXML
	private Label consumedMessages;
	@FXML
	private Label bitcoinMessages;
	@FXML
	private Label sportMessages;
	@FXML
	private JFXButton startBtn;
	@FXML
	private BarChart<String, Integer> table;

	private void runConsumer(final ActionEvent event) {
		final StatusProducer actualStatus = model.getStatus();
		final StatusProducer nextStatus;

		if (STOPPED.equals(actualStatus)) {
			nextStatus = STARTED;
			service.startProducer();
		} else {
			nextStatus = STOPPED;
			service.stopProducer();
		}

		startBtn.setText(nextStatus.getTextButton());
		model.setStatus(nextStatus);
	}

	public void disposeConsumers() {
		model.getTopics().values().forEach(d -> {
			log.info("****** [Dispose topic {}]", d.getTopicName());
			d.getDisposable().dispose();
		});
	}

	@Override
	public void initialize(final URL url, final ResourceBundle resourceBundle) {
		final Topic topicBitcoin = model.getTopics().get(TOPIC_BITCOIN);
		final Topic topicSport = model.getTopics().get(TOPIC_SPORT);

		final TableSubscriber subscriberBitcoin = new TableSubscriber(topicBitcoin, service);
		final TableSubscriber subscriberSport = new TableSubscriber(topicSport, service);

		final StringConverter<Number> converter = new NumberStringConverter();

		model.getData().add(subscriberBitcoin.getSeries());
		model.getData().add(subscriberSport.getSeries());
		table.setData(model.getData());

		startBtn.setOnAction(this::runConsumer);

		model.getTot().bind(topicBitcoin.getCount().add(topicSport.getCount()));

		bitcoinMessages.textProperty().bindBidirectional(topicBitcoin.getCount(), converter);
		consumedMessages.textProperty().bindBidirectional(model.getTot(), converter);
		sportMessages.textProperty().bindBidirectional(topicSport.getCount(), converter);

		consumedMessages.textProperty().addListener((s, oldVal, newVal) -> consumedMessages.setText(newVal));
		bitcoinMessages.textProperty().addListener((s, oldVal, newVal) -> bitcoinMessages.setText(newVal));
		sportMessages.textProperty().addListener((s, oldVal, newVal) -> sportMessages.setText(newVal));
	}

	public ConsumerWindow(final ConsumerService service) {
		this.service = service;
		this.model = new ConsumerModel();
	}
}
