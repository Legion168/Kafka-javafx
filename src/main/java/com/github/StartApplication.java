package com.github;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.ui.application.JavaFxApplication;

import javafx.application.Application;

@SpringBootApplication
public class StartApplication {
    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
}
