package com.svedentsov.kafka.helper.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

/**
 * Стратегия, устанавливающая смещение на самое первое доступное сообщение в каждой назначенной партиции.
 * Полезна для сценариев, когда необходимо гарантированно прочитать и обработать все сообщения в топике с самого начала.
 */
@Slf4j
public class EarliestStartStrategy implements ConsumerStartStrategy {

    @Override
    public void apply(KafkaConsumer<String, ?> consumer, Collection<TopicPartition> partitions, String topicName) {
        log.info("Применение стратегии EARLIEST для топика '{}'. Смещение в начало для {} партиций.", topicName, partitions.size());
        consumer.seekToBeginning(partitions);
    }
}
