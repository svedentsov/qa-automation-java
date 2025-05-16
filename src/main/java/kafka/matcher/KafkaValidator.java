package kafka.matcher;

import kafka.exception.ValidationException;
import kafka.matcher.assertions.CompositeAssertions;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * Класс для валидации одной или нескольких записей Apache Kafka с применением заданных проверок.
 * Все записи объединяются в единый список, что позволяет выполнять проверки единообразно.
 *
 * @param <T> тип записи для валидации
 */
@Slf4j
public final class KafkaValidator<T> {

    /**
     * Список записей для проверки.
     */
    private final List<T> records;

    /**
     * Конструктор для валидации единственной записи.
     *
     * @param record Запись для проверки
     */
    public KafkaValidator(@NonNull T record) {
        this(Collections.singletonList(record));
    }

    /**
     * Конструктор для валидации списка записей.
     *
     * @param records Список записей для проверки
     */
    public KafkaValidator(@NonNull List<T> records) {
        this.records = records;
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
