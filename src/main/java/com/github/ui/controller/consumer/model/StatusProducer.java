package com.github.ui.controller.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusProducer {
    STARTED("Stop"),
    STOPPED("Start");

    private final String textButton;
}
