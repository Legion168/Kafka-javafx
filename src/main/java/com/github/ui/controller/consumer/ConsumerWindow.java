package com.github.ui.controller.consumer;

import static javafx.collections.FXCollections.observableArrayList;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.scene.chart.BarChart;
import org.springframework.stereotype.Component;

import com.github.client.model.Topic;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
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
	private final ToggleGroup tg;

	private Boolean started = false;

	@FXML
	private Label consumedMessages;
	@FXML
	private Label bitcoinMessages;
	@FXML
	private Label sportMessages;
	@FXML
	private JFXRadioButton bitcoin;
	@FXML
	private JFXRadioButton sport;
	@FXML
	private JFXRadioButton all;
	@FXML
	private JFXButton startBtn;
	@FXML
	private BarChart<String, Integer> table;

	@FXML
	private void runConsumer(final ActionEvent event) {
		if (!started) {
			startBtn.setText("Stop");
			started = true;
			service.runProducer();
		} else {
			startBtn.setText("Start");
			started = false;
			service.stopConsumer();
		}
	}

	@Override
	public void initialize(final URL url, final ResourceBundle resourceBundle) {
		final TableSubscriber subscriber = new TableSubscriber();

		final ObservableList<XYChart.Series<String, Integer>> data = observableArrayList();
		final StringConverter<Number> converter = new NumberStringConverter();

		data.add(subscriber.getSeries());
		table.setData(data);

		startBtn.setOnAction(this::runConsumer);

		bitcoinMessages.textProperty().bindBidirectional(model.getBitcoin(), converter);
		consumedMessages.textProperty().bindBidirectional(model.getTot(), converter);
		sportMessages.textProperty().bindBidirectional(model.getSport(), converter);

		consumedMessages.textProperty().addListener((s, oldVal, newVal) -> consumedMessages.setText(newVal));
		bitcoinMessages.textProperty().addListener((s, oldVal, newVal) -> bitcoinMessages.setText(newVal));
		sportMessages.textProperty().addListener((s, oldVal, newVal) -> sportMessages.setText(newVal));

		bitcoin.setToggleGroup(tg);
		sport.setToggleGroup(tg);
		all.setToggleGroup(tg);

		table.getXAxis().setTickLabelFill(Color.WHITE);
	}

	public ConsumerWindow(final ConsumerService service) {
		this.service = service;
		this.model = new ConsumerModel();
		this.tg = new ToggleGroup();
	}

	private class TableSubscriber implements Consumer<Topic> {
		private final ObservableList<XYChart.Data<String, Integer>> seriesData = observableArrayList();
		private final XYChart.Series<String, Integer> series;

		public XYChart.Series<String, Integer> getSeries() {
			return series;
		}

		private TableSubscriber() {
			series = new XYChart.Series<>("", seriesData);
			service.runConsumer(model).subscribe(this);
		}

		@Override
		public void accept(final Topic topic) {
			Platform.runLater(() -> {
				if ("bitcoin".equalsIgnoreCase(topic.getTopic()))
					seriesData.add(new XYChart.Data<>(topic.getTopic(), model.getBitcoin().intValue()));
				else
					seriesData.add(new XYChart.Data<>(topic.getTopic(), model.getSport().intValue()));
			});
		}
	}
}
