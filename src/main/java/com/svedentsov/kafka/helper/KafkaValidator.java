package com.svedentsov.kafka.helper;

import com.svedentsov.matcher.Condition;
import com.svedentsov.matcher.assertions.CompositeAssertions;
import com.svedentsov.kafka.exception.ValidationException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Класс для валидации одной или нескольких записей Apache Kafka с применением заданных проверок.
 * Все записи объединяются в единый список, что позволяет выполнять проверки единообразно.
 *
 * @param <T> тип проверяемой записи
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaValidator<T> {

    /**
     * Список записей для проверки.
     */
    private final List<T> records;

    /**
     * Создаёт валидатор для одной записи.
     *
     * @param record запись для проверки, не должна быть null
     * @param <T>    тип записи
     * @return валидатор
     * @throws NullPointerException если {@code record} == null
     */
    public static <T> KafkaValidator<T> forRecords(@Nonnull T record) {
        Objects.requireNonNull(record, "Запись для валидации не может быть null");
        return new KafkaValidator<>(Collections.singletonList(record));
    }

    /**
     * Конструктор для валидации списка записей.
     *
     * @param records Список записей для проверки
     */
    public static <T> KafkaValidator<T> forRecords(@Nonnull List<T> records) {
        Objects.requireNonNull(records, "Список записей не может быть null");
        return new KafkaValidator<>(records);
    }

    /**
     * Применяет заданные проверки к каждой записи.
     *
     * @param conditions Набор проверок для валидации записей
     * @return Текущий экземпляр KafkaValidator для построения цепочки вызовов
     */
    @SafeVarargs
    public final KafkaValidator<T> shouldHave(@NonNull Condition<T>... conditions) {
        Condition<T> composite = CompositeAssertions.and(conditions);
        log.debug("Проверка условия '{}' для {} записей", composite, records.size());
        records.forEach(record -> executeCheck(() -> composite.check(record), composite, "запись"));
        return this;
    }

    @SafeVarargs
    public final KafkaValidator<T> shouldHaveList(@NonNull Condition<List<T>>... conditions) {
        Condition<List<T>> composite = CompositeAssertions.and(conditions);
        log.debug("Проверка списка условий '{}' для {} записей", composite, records.size());
        executeCheck(() -> composite.check(records), composite, "список записей");
        return this;
    }

    /**
     * Выполняет проверку и обрабатывает исключения, возникающие при выполнении условия.
     *
     * @param check       проверка, представленная в виде {@code Runnable}
     * @param condition   условие, которое проверяется (для формирования сообщения об ошибке)
     * @param recordLabel идентификатор или описание записи (или группы записей)
     */
    private void executeCheck(Runnable check, Object condition, String recordLabel) {
        try {
            check.run();
        } catch (AssertionError error) {
            String errorMessage = String.format("Условие %s не выполнено для [%s]: %s", condition, recordLabel, error.getMessage());
            throw new ValidationException(errorMessage, error);
        } catch (Exception exception) {
            String errorMessage = String.format("Ошибка при проверке условия %s для [%s]: %s", condition, recordLabel, exception.getMessage());
            throw new ValidationException(errorMessage, exception);
        }
    }
}
