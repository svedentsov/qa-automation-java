package com.svedentsov.kafka.helper.strategy;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

/**
 * Интерфейс стратегии, определяющий начальную позицию (смещение) для Kafka-консьюмера
 * после назначения ему партиций.
 */
@FunctionalInterface
public interface ConsumerStartStrategy {

    /**
     * Применяет логику смещения к консьюмеру для указанных партиций.
     * Этот метод вызывается каждый раз, когда консьюмеру назначаются партиции.
     *
     * @param consumer   Экземпляр Kafka-консьюмера.
     * @param partitions Коллекция партиций, назначенных консьюмеру.
     * @param topicName  Имя топика для логирования и контекста.
     */
    void apply(KafkaConsumer<String, ?> consumer, Collection<TopicPartition> partitions, String topicName);
}
