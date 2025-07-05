package com.svedentsov.kafka.helper.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Стратегия, которая устанавливает смещение на конкретное, заданное значение для каждой партиции.
 * Позволяет точно позиционировать консьюмер. Если для какой-либо из назначенных партиций
 * смещение не указано в предоставленной карте, для этой партиции смещение не будет изменено,
 * и будет использовано поведение по умолчанию (сохраненное смещение или auto.offset.reset).
 * <p><b>Сценарий использования:</b> Идеально для регрессионного тестирования и отладки,
 * когда необходимо начать чтение с того же самого сообщения, которое вызвало проблему.</p>
 */
@Slf4j
public class FromSpecificOffsetStartStrategy implements ConsumerStartStrategy {

    private final Map<Integer, Long> partitionOffsets;

    /**
     * Создает экземпляр стратегии.
     *
     * @param partitionOffsets Карта, где ключ - номер партиции, а значение - целевое смещение (offset).
     */
    public FromSpecificOffsetStartStrategy(Map<Integer, Long> partitionOffsets) {
        this.partitionOffsets = requireNonNull(partitionOffsets, "Карта смещений (partitionOffsets) не может быть null.");
    }

    @Override
    public void apply(KafkaConsumer<String, ?> consumer, Collection<TopicPartition> partitions, String topicName) {
        log.info("Применение стратегии FROM_SPECIFIC_OFFSET для топика '{}'.", topicName);
        for (TopicPartition partition : partitions) {
            Integer partitionNum = partition.partition();
            Long targetOffset = partitionOffsets.get(partitionNum);

            if (targetOffset != null) {
                log.info("Для топика '{}', партиция {} будет смещена на указанное смещение {}.", topicName, partitionNum, targetOffset);
                consumer.seek(partition, targetOffset);
            } else {
                log.warn("Для топика '{}', партиция {} не имеет указанного смещения в карте. " +
                        "Будет использовано смещение по умолчанию (committed или auto.offset.reset).", topicName, partitionNum);
            }
        }
    }
}
