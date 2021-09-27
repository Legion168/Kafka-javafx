package com.github.ui.controller.consumer.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.Disposable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Topic {
    private String topicName;

    private String showedName;

    @Builder.Default
    private IntegerProperty count = new SimpleIntegerProperty(0);

    @Builder.Default
    private Disposable disposable = null;
}
