package com.svedentsov.kafka.helper.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

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
    public void apply(KafkaConsumer<String, ?> consumer, Collection<TopicPartition> partitions, String topicName) {
        log.info("Применение стратегии LATEST для топика '{}'. Смещение в конец {} партиций.", topicName, partitions.size());
        consumer.seekToEnd(partitions);
    }
}
