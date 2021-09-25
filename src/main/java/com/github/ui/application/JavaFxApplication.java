package com.github.ui.application;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.github.StartApplication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class JavaFxApplication extends Application {
	private ConfigurableApplicationContext context;

	@Override
	public void start(final Stage stage) {
		context.publishEvent(new StageReadyEvent(stage));
	}

	@Override
	public void init() {
		final String[] args = getParameters().getRaw().toArray(new String[0]);

		this.context = new SpringApplicationBuilder().sources(StartApplication.class).run(args);
	}

	@Override
	public void stop() {
		this.context.close();
		Platform.exit();
	}
}
