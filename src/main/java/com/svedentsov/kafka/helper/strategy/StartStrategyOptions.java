package com.svedentsov.kafka.helper.strategy;

import com.svedentsov.kafka.enums.StartStrategyType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;

/**
 * <p>Класс {@code StartStrategyOptions} инкапсулирует параметры, необходимые для различных
 * стратегий начального смещения (offset) потребителя Kafka.</p>
 *
 * <p>Использование билдера (через {@code @Builder} Lombok) позволяет гибко и читаемо
 * создавать объекты с необходимыми параметрами в зависимости от выбранной {@link StartStrategyType}.</p>
 *
 * <p>Примеры использования:</p>
 * <pre>{@code
 * // Стратегия LATEST (без дополнительных параметров)
 * StartStrategyOptions latestOptions = StartStrategyOptions.builder()
 * .strategyType(StartStrategyType.LATEST)
 * .build();
 *
 * // Стратегия FROM_TIMESTAMP
 * StartStrategyOptions timestampOptions = StartStrategyOptions.builder()
 * .strategyType(StartStrategyType.FROM_TIMESTAMP)
 * .lookBackDuration(Duration.ofHours(1))
 * .build();
 *
 * // Стратегия FROM_SPECIFIC_OFFSET
 * Map<Integer, Long> offsets = Map.of(0, 100L, 1, 200L);
 * StartStrategyOptions specificOffsetOptions = StartStrategyOptions.builder()
 * .strategyType(StartStrategyType.FROM_SPECIFIC_OFFSET)
 * .partitionOffset(0, 100L) // Пример использования @Singular
 * .partitionOffset(1, 200L)
 * .build();
 *
 * // Стратегия RELATIVE_FROM_END
 * StartStrategyOptions relativeOptions = StartStrategyOptions.builder()
 * .strategyType(StartStrategyType.RELATIVE_FROM_END)
 * .offsetFromEnd(50L) // Последние 50 сообщений
 * .build();
 * }</pre>
 */
@Getter
@Builder
@Slf4j
public class StartStrategyOptions {

    /**
     * Тип стратегии начального смещения, определяющий, как Kafka-потребитель
     * должен начать читать сообщения из топика.
     * Не может быть {@code null}.
     */
    @NonNull
    private final StartStrategyType strategyType;

    /**
     * Продолжительность, на которую нужно "оглянуться" назад от текущего времени,
     * чтобы найти начальное смещение.
     * Используется только для стратегии {@link StartStrategyType#FROM_TIMESTAMP}.
     * Если указано для другой стратегии, будет проигнорировано с предупреждением.
     */
    private Duration lookBackDuration;

    /**
     * Карта, где ключ - номер партиции, а значение - целевое смещение (offset),
     * с которого нужно начать чтение для данной партиции.
     * Используется только для стратегии {@link StartStrategyType#FROM_SPECIFIC_OFFSET}.
     * Если указано для другой стратегии, будет проигнорировано с предупреждением.
     * <p>Использование {@code @Singular} позволяет добавлять элементы в карту по одному
     * или передавать полную карту.</p>
     */
    @Singular("partitionOffset") // Позволяет добавлять элементы в карту билдером: .partitionOffset(0, 123L)
    private Map<Integer, Long> partitionOffsets;

    /**
     * Количество сообщений от конца партиции, с которого нужно начать чтение.
     * Например, значение 10 означает "начать с 10-го сообщения с конца".
     * Должно быть положительным числом.
     * Используется только для стратегии {@link StartStrategyType#RELATIVE_FROM_END}.
     * Если указано для другой стратегии, будет проигнорировано с предупреждением.
     */
    private Long offsetFromEnd;

    /**
     * Метод, который можно вызвать после создания объекта для выполнения логической проверки.
     * Lombok @Builder может быть расширен для вызова такого метода.
     * Для упрощения, проверку параметров мы оставили в createConsumerStartStrategy.
     */
    public void validate() {
        if (strategyType != StartStrategyType.FROM_TIMESTAMP && lookBackDuration != null) {
            log.warn("Параметр 'lookBackDuration' указан для стратегии {}, но будет проигнорирован, так как он применим только для {}.",
                    strategyType, StartStrategyType.FROM_TIMESTAMP);
        }
        if (strategyType != StartStrategyType.FROM_SPECIFIC_OFFSET && (partitionOffsets != null && !partitionOffsets.isEmpty())) {
            log.warn("Параметр 'partitionOffsets' указан для стратегии {}, но будет проигнорирован, так как он применим только для {}.",
                    strategyType, StartStrategyType.FROM_SPECIFIC_OFFSET);
        }
        if (strategyType != StartStrategyType.RELATIVE_FROM_END && offsetFromEnd != null) {
            log.warn("Параметр 'offsetFromEnd' указан для стратегии {}, но будет проигнорирован, так как он применим только для {}.",
                    strategyType, StartStrategyType.RELATIVE_FROM_END);
        }
    }
}
