package com.svedentsov.steps.manager;

import com.svedentsov.steps.common.KafkaSteps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaManager {

    private static KafkaManager kafkaManager;
    private KafkaSteps kafkaSteps;

    public synchronized static KafkaManager getManager() {
        return Optional.ofNullable(kafkaManager).orElseGet(() -> kafkaManager = new KafkaManager());
    }

    public KafkaSteps steps() {
        return Optional.ofNullable(kafkaSteps).orElseGet(() -> kafkaSteps = new KafkaSteps());
    }
}
