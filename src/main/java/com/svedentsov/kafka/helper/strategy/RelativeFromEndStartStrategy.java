package com.svedentsov.kafka.helper.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.Map;

/**
 * Стратегия, устанавливающая смещение относительно конца партиции.
 * Например, позволяет начать чтение с "последних N" сообщений.
 * <p><b>Сценарий использования:</b> Отлично подходит для smoke-тестов, где нужно проверить
 * только самые последние данные в топике, игнорируя всю предыдущую историю, что значительно
 * ускоряет выполнение теста.</p>
 */
@Slf4j
public class RelativeFromEndStartStrategy implements ConsumerStartStrategy {

    private final long offsetFromEnd;

    /**
     * Создает экземпляр стратегии.
     *
     * @param offsetFromEnd Количество сообщений от конца, с которого нужно начать чтение.
     *                      Например, значение 10 означает "начать с 10-го сообщения с конца".
     *                      Должно быть положительным числом.
     */
    public RelativeFromEndStartStrategy(long offsetFromEnd) {
        if (offsetFromEnd <= 0) {
            throw new IllegalArgumentException("Смещение от конца (offsetFromEnd) должно быть положительным.");
        }
        this.offsetFromEnd = offsetFromEnd;
    }

    @Override
    public void apply(KafkaConsumer<String, ?> consumer, Collection<TopicPartition> partitions, String topicName) {
        log.info("Применение стратегии RELATIVE_FROM_END для топика '{}'. Чтение последних {} сообщений.", topicName, offsetFromEnd);

        // Сначала получаем самые последние смещения для всех назначенных партиций.
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);

        for (TopicPartition partition : partitions) {
            Long endOffset = endOffsets.get(partition);
            if (endOffset != null) {
                // Вычисляем целевое смещение. Оно не может быть меньше 0.
                long targetOffset = Math.max(0, endOffset - offsetFromEnd);
                log.info("Для топика '{}', партиция {}: конец на смещении {}, целевое смещение {}.",
                        topicName, partition.partition(), endOffset, targetOffset);
                consumer.seek(partition, targetOffset);
            } else {
                log.warn("Не удалось определить конечное смещение для топика '{}', партиция {}. " +
                        "Смещение для этой партиции не будет изменено.", topicName, partition.partition());
            }
        }
    }
}
