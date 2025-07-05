package com.svedentsov.kafka.helper.strategy;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

/**
 * Функциональный интерфейс, определяющий стратегию установки начального смещения (offset)
 * для Kafka-консьюмера при назначении ему новых партиций.
 * Это позволяет гибко управлять тем, с какого момента в топике начинать чтение сообщений.
 */
@FunctionalInterface
public interface ConsumerStartStrategy {

    /**
     * Применяет стратегию к консьюмеру для указанных партиций.
     *
     * @param consumer   Экземпляр KafkaConsumer, к которому применяется стратегия.
     * @param partitions Коллекция партиций (TopicPartition), которые были назначены консьюмеру.
     * @param topicName  Имя топика, для которого применяются настройки.
     */
    void apply(KafkaConsumer<String, ?> consumer, Collection<TopicPartition> partitions, String topicName);
}
