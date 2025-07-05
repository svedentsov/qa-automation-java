package com.svedentsov.kafka.helper.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

/**
 * Реализация {@link ConsumerStartStrategy}, которая смещает Kafka Consumer
 * в начало всех назначенных партиций, начиная чтение с самых старых сообщений.
 */
@Slf4j
public class EarliestStartStrategy implements ConsumerStartStrategy {

    /**
     * Применяет стратегию смещения в начало (seekToBeginning) для всех назначенных партиций.
     *
     * @param consumer   Экземпляр {@link KafkaConsumer}.
     * @param partitions Набор {@link TopicPartition}, назначенных потребителю.
     * @param topicName  Имя топика, для которого применяется стратегия.
     */
    @Override
    public void apply(KafkaConsumer<String, ?> consumer, Collection<TopicPartition> partitions, String topicName) {
        log.info("Применение стратегии EARLIEST для топика '{}'. Смещение в начало {} партиций.", topicName, partitions.size());
        consumer.seekToBeginning(partitions);
    }
}
