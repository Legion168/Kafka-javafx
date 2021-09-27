package com.github.ui.application;

import static javafx.stage.StageStyle.DECORATED;
import static javafx.stage.StageStyle.UNDECORATED;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.github.ui.controller.MainWindow;

import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;

@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {
    private final FxWeaver fxWeaver;
    private double xOffset;
    private double yOffset;

    @Override
    public void onApplicationEvent(final StageReadyEvent event) {
        final Stage stage = event.stage;
        stage.setTitle("Kafka Application");

        final Scene scene = new Scene(fxWeaver.loadView(MainWindow.class));
        stage.setScene(scene);

        if (stage.getStyle().equals(DECORATED))
            stage.initStyle(UNDECORATED);

        stage.getScene().setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        stage.getScene().setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });

        stage.sizeToScene();
        stage.show();
    }

    public PrimaryStageInitializer(final FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }
}
