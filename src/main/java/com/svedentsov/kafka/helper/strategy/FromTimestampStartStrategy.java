package com.svedentsov.kafka.helper.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Стратегия, устанавливающая смещение на первое сообщение, временная метка которого больше или равна указанной.
 * Временная метка рассчитывается как (текущее время - lookBackDuration).
 * Полезна для обработки сообщений за определенный недавний период времени (например, "за последний час").
 */
@Slf4j
public class FromTimestampStartStrategy implements ConsumerStartStrategy {

    private final Duration lookBackDuration;

    /**
     * Создает новый экземпляр стратегии на основе временной метки.
     *
     * @param lookBackDuration Продолжительность, на которую нужно "оглянуться" назад от текущего времени,
     *                         чтобы определить целевую временную метку для поиска смещения.
     */
    public FromTimestampStartStrategy(Duration lookBackDuration) {
        this.lookBackDuration = requireNonNull(lookBackDuration, "lookBackDuration не может быть null для FromTimestampStartStrategy.");
    }

    @Override
    public void apply(KafkaConsumer<String, ?> consumer, Collection<TopicPartition> partitions, String topicName) {
        long targetTimestamp = Instant.now().minus(lookBackDuration).toEpochMilli();
        log.info("Применение стратегии FROM_TIMESTAMP для топика '{}'. Целевая временная метка: {}. Поиск смещений...", topicName, targetTimestamp);

        Map<TopicPartition, Long> timestampsToSearch = partitions.stream()
                .collect(Collectors.toMap(p -> p, p -> targetTimestamp));

        Map<TopicPartition, OffsetAndTimestamp> offsets = consumer.offsetsForTimes(timestampsToSearch);

        for (TopicPartition partition : partitions) {
            OffsetAndTimestamp offsetAndTimestamp = offsets.get(partition);
            if (offsetAndTimestamp != null) {
                consumer.seek(partition, offsetAndTimestamp.offset());
                log.info("Для топика '{}', партиция {} смещена на смещение {} (timestamp: {}).", topicName, partition.partition(), offsetAndTimestamp.offset(), offsetAndTimestamp.timestamp());
            } else {
                // Если для временной метки нет смещения, это значит, что все сообщения в партиции старше.
                // В этом случае, согласно документации Kafka, consumer.offsetsForTimes вернет null.
                // Логичным поведением будет сместиться в конец, чтобы читать только новые сообщения.
                consumer.seekToEnd(List.of(partition));
                log.warn("Для топика '{}', партиция {} не найдено смещения по timestamp {}. Смещен в конец партиции.", topicName, partition.partition(), targetTimestamp);
            }
        }
    }
}
