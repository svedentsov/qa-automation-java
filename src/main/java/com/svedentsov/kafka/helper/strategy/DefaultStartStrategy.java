package com.svedentsov.kafka.helper.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

/**
 * Стратегия, которая явно делегирует управление смещением самому Kafka.
 * Это стандартное поведение:
 * 1. Если для данной группы потребителей (`group.id`) уже есть сохраненное (закоммиченное) смещение,
 * консьюмер начнет чтение со следующего сообщения после сохраненного.
 * 2. Если сохраненного смещения нет (например, новая группа потребителей), будет применена
 * политика, указанная в конфигурации `auto.offset.reset` (`latest`, `earliest` или `none`).
 * <p><b>Сценарий использования:</b> Применяется, когда тест должен симулировать поведение
 * обычного сервиса-потребителя, который продолжает работу с того места, где остановился.</p>
 */
@Slf4j
public class DefaultStartStrategy implements ConsumerStartStrategy {

    @Override
    public void apply(KafkaConsumer<String, ?> consumer, Collection<TopicPartition> partitions, String topicName) {
        log.info("Применение стратегии DEFAULT для топика '{}' на {} партиций. " +
                        "Будет использовано сохраненное смещение или политика 'auto.offset.reset'.",
               topicName, partitions.size());
    }
}
