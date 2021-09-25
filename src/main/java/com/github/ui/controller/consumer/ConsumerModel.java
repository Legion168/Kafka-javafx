package com.github.ui.controller.consumer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsumerModel {
    @Builder.Default
    private IntegerProperty tot = new SimpleIntegerProperty(0);

    @Builder.Default
    private IntegerProperty bitcoin = new SimpleIntegerProperty(0);

    @Builder.Default
    private IntegerProperty sport = new SimpleIntegerProperty(0);
}
