package com.svedentsov.kafka.helper.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Set;

/**
 * Реализация {@link ConsumerStartStrategy}, которая смещает Kafka Consumer
 * в конец всех назначенных партиций, начиная чтение только новых сообщений.
 */
@Slf4j
public class LatestStartStrategy implements ConsumerStartStrategy {

    /**
     * Применяет стратегию смещения в конец (seekToEnd) для всех назначенных партиций.
     *
     * @param consumer   Экземпляр {@link KafkaConsumer}.
     * @param partitions Набор {@link TopicPartition}, назначенных потребителю.
     * @param topicName  Имя топика, для которого применяется стратегия.
     */
    @Override
    public void apply(KafkaConsumer<String, ?> consumer, Set<TopicPartition> partitions, String topicName) {
        consumer.seekToEnd(partitions);
        log.info("Consumer для топика '{}' смещен в конец всех {} партиций (LATEST).", topicName, partitions.size());
    }
}
