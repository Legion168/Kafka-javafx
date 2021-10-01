package com.github.ui.controller.consumer.model;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.chart.XYChart.Data;
import static javafx.scene.chart.XYChart.Series;

import java.util.function.Consumer;

import com.github.ui.controller.consumer.service.ConsumerService;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import reactor.core.Disposable;

public class TableSubscriber implements Consumer<Topic> {
	private final Series<String, Integer> series;
	private final ObservableList<Data<String, Integer>> seriesData = observableArrayList();

	public Series<String, Integer> getSeries() {
		return series;
	}

	public TableSubscriber(final Topic topic, final ConsumerService service) {
		this.series = new Series<>("", seriesData);

		final Disposable disposable = service.runConsumer(topic).subscribe(this);
		topic.setDisposable(disposable);
	}

	@Override
	public void accept(final Topic topic) {
		Platform.runLater(() -> seriesData.add(new Data<>(topic.getShowedName(), topic.getCount().intValue())));
	}
}
