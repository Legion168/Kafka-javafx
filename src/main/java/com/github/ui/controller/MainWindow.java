package com.github.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.github.ui.controller.consumer.ConsumerWindow;

import io.vavr.control.Try;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;

@Slf4j
@FxmlView
@Component
public class MainWindow implements Initializable {
	private final MainService service;
	private final ConsumerWindow consumerWindow;

	@FXML
	private Label titleSection;

	@FXML
	private BorderPane mainPane;

	@Override
	public void initialize(final URL url, final ResourceBundle resourceBundle) {
		Try.run(this::handleConsumer).onFailure(ex -> log.error("****** [Error opening Dashboard: {0}]", ex));
	}

	@FXML
	private void handleExit() {
		System.exit(0);
	}

	@FXML
	private void handleDashboard() {
		titleSection.setText("Dashboard");
	}

	@FXML
	private void handleConsumer() {
		titleSection.setText("Consumer");

		final StackPane pane;

		final FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/com/github/ui/controller/consumer/ConsumerWindow.fxml"));
		loader.setControllerFactory(p -> consumerWindow);

		final Parent root = Try
				.of(loader::load)
				.map(Parent.class::cast)
				.onFailure(Throwable::printStackTrace)
				.getOrNull();

		pane = (StackPane) root;

		mainPane.setRight(pane);
	}

	@FXML
	private void handleProducer() {
		titleSection.setText("Producer");
	}

	@FXML
	private void handleClient() {
		titleSection.setText("Client");
	}

	@FXML
	private void handleManage() {
		titleSection.setText("Manage");
	}

	@FXML
	private void handleStream() {
		titleSection.setText("Stream");
	}

	public MainWindow(MainService service, ConsumerWindow consumerWindow) {
		this.service = service;
		this.consumerWindow = consumerWindow;
	}
}
