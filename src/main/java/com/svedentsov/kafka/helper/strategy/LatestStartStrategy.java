package com.svedentsov.kafka.helper.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

/**
 * Стратегия, устанавливающая смещение на следующее сообщение, которое будет записано в партицию (т.е., в самый конец).
 * Используется, когда нужно игнорировать все старые сообщения и обрабатывать только новые, поступающие после запуска слушателя.
 */
@Slf4j
public class LatestStartStrategy implements ConsumerStartStrategy {

    @Override
    public void apply(KafkaConsumer<String, ?> consumer, Collection<TopicPartition> partitions, String topicName) {
        log.info("Применение стратегии LATEST для топика '{}'. Смещение в конец для {} партиций.", topicName, partitions.size());
        consumer.seekToEnd(partitions);
    }
}
