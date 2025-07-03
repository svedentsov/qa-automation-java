package com.svedentsov.kafka.helper.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Set;

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
    public void apply(KafkaConsumer<String, ?> consumer, Set<TopicPartition> partitions, String topicName) {
        consumer.seekToBeginning(partitions);
        log.info("Consumer для топика '{}' смещен в начало всех {} партиций (EARLIEST).", topicName, partitions.size());
    }
}
