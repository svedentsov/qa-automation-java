package com.svedentsov.kafka.helper.strategy;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Set;

/**
 * Определяет контракт для стратегий запуска Kafka Consumer.
 * Каждая стратегия отвечает за позиционирование потребителя на определенном смещении
 * в назначенных ему партициях.
 */
public interface ConsumerStartStrategy {

    /**
     * Применяет стратегию позиционирования к заданному Kafka Consumer и его партициям.
     *
     * @param consumer   Экземпляр {@link KafkaConsumer}, к которому применяется стратегия.
     * @param partitions Набор {@link TopicPartition}, назначенных данному потребителю.
     * @param topicName  Имя топика, для которого применяется стратегия (для логирования).
     * @throws IllegalArgumentException если параметры стратегии недействительны.
     */
    void apply(KafkaConsumer<String, ?> consumer, Set<TopicPartition> partitions, String topicName);
}
