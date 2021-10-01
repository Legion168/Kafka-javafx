package com.github.ui.controller.consumer.model;

import java.util.Map;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.common.Constant.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsumerModel {
	@Builder.Default
	private IntegerProperty tot = new SimpleIntegerProperty(0);

	@Builder.Default
	private StatusProducer status = StatusProducer.STOPPED;

	@Builder.Default
	private ObservableList<XYChart.Series<String, Integer>> data = FXCollections.observableArrayList();

	@Builder.Default
	private Map<String, Topic> topics = Map
			.of( //
					TOPIC_BITCOIN, Topic.builder().topicName(TOPIC_BITCOIN).showedName("#Bitcoin").build(), //
					TOPIC_SPORT, Topic.builder().topicName(TOPIC_SPORT).showedName("#Sport").build(), //
					TOPIC_ALIEN, Topic.builder().topicName(TOPIC_ALIEN).showedName("#Alien").build(), //
					TOPIC_PIZZA, Topic.builder().topicName(TOPIC_PIZZA).showedName("#Pizza").build() //
			);
}
