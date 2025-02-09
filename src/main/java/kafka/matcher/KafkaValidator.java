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
        Condition<T> compositeCondition = CompositeAssertions.and(conditions);
        log.debug("Проверка условия '{}' для {} записей", compositeCondition, records.size());
        execute(() -> compositeCondition.checkAll(records), compositeCondition, "records");
        return this;
    }

    private void execute(Runnable check, Object condition, String recordLabel) {
        try {
            check.run();
        } catch (AssertionError error) {
            String errorMessage = String.format("Условие %s не выполнено для [%s]: %s",
                    condition, recordLabel, error.getMessage());
            throw new ValidationException(errorMessage, error);
        } catch (Exception exception) {
            String errorMessage = String.format("Ошибка при проверке условия %s для [%s]: %s",
                    condition, recordLabel, exception.getMessage());
            throw new ValidationException(errorMessage, exception);
        }
    }
}
