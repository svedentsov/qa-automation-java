package com.svedentsov.kafka.helper.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Реализация {@link ConsumerStartStrategy}, которая смещает Kafka Consumer
 * к смещению, соответствующему определенной временной метке, отсчитываемой
 * назад от текущего момента на заданную продолжительность.
 */
@Slf4j
public class FromTimestampStartStrategy implements ConsumerStartStrategy {

    private final Duration lookBackDuration;

    /**
     * Создает новый экземпляр стратегии на основе временной метки.
     *
     * @param lookBackDuration Продолжительность, на которую нужно "оглянуться" назад от текущего времени,
     *                         чтобы определить целевую временную метку для поиска смещения. Не может быть null.
     */
    public FromTimestampStartStrategy(Duration lookBackDuration) {
        this.lookBackDuration = requireNonNull(lookBackDuration, "lookBackDuration не может быть null для FromTimestampStartStrategy.");
    }

    /**
     * Применяет стратегию смещения по временной метке для всех назначенных партиций.
     * Ищет ближайшее смещение для каждой партиции, которое соответствует или превышает
     * вычисленную временную метку. Если смещение не найдено (например, метка слишком старая),
     * то потребитель смещается в начало партиции.
     *
     * @param consumer   Экземпляр {@link KafkaConsumer}.
     * @param partitions Набор {@link TopicPartition}, назначенных потребителю.
     * @param topicName  Имя топика, для которого применяется стратегия.
     */
    @Override
    public void apply(KafkaConsumer<String, ?> consumer, Set<TopicPartition> partitions, String topicName) {
        long targetTimestamp = Instant.now().minus(lookBackDuration).toEpochMilli();
        Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
        for (TopicPartition partition : partitions) {
            timestampsToSearch.put(partition, targetTimestamp);
        }

        Map<TopicPartition, OffsetAndTimestamp> offsets = consumer.offsetsForTimes(timestampsToSearch);
        for (TopicPartition partition : partitions) {
            OffsetAndTimestamp offsetAndTimestamp = offsets.get(partition);
            if (offsetAndTimestamp != null) {
                consumer.seek(partition, offsetAndTimestamp.offset());
                log.info("Consumer для топика '{}', партиция {} смещен на смещение {} (timestamp: {}).",
                        topicName, partition.partition(), offsetAndTimestamp.offset(), offsetAndTimestamp.timestamp());
            } else {
                consumer.seekToBeginning(Collections.singleton(partition));
                log.warn("Для топика '{}', партиция {} не найдено смещения по timestamp {}. Смещен в начало.",
                        topicName, partition.partition(), targetTimestamp);
            }
        }
    }
}
