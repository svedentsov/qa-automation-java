package db.matcher;

import core.matcher.Condition;
import core.matcher.assertions.CompositeAssertions;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * Класс для валидации одной или нескольких записей с применением заданных условий.
 * Все записи объединяются в один список, что позволяет единообразно применять проверки.
 *
 * @param <T> тип записи для валидации
 */
@Slf4j
public final class DbValidator<T> {

    /**
     * Список записей для валидации.
     */
    private final List<T> records;

    /**
     * Конструктор для валидации единственной записи.
     *
     * @param record запись для валидации
     */
    public DbValidator(@NonNull T record) {
        this(Collections.singletonList(record));
    }

    /**
     * Конструктор для валидации списка записей.
     *
     * @param records список записей для валидации
     */
    public DbValidator(@NonNull List<T> records) {
        this.records = records;
    }

    /**
     * Применяет переданные условия проверки ко всем записям.
     * Все условия объединяются с помощью логической операции И (AND).
     *
     * @param conditions набор условий проверки записей
     * @return текущий экземпляр DbValidator для дальнейшей цепочки вызовов
     */
    @SafeVarargs
    public final DbValidator<T> shouldHave(@NonNull Condition<T>... conditions) {
        Condition<T> composite = CompositeAssertions.and(conditions);
        log.debug("Проверка условия '{}' для {} записей", composite, records.size());
        records.forEach(record -> executeCheck(() -> composite.check(record), composite, "запись"));
        return this;
    }

    /**
     * Применяет переданные условия проверки ко всем записям в виде списка.
     * Все условия объединяются с помощью логической операции И (AND).
     *
     * @param conditions набор условий проверки записей в виде списка
     * @return текущий экземпляр DbValidator для дальнейшей цепочки вызовов
     */
    @SafeVarargs
    public final DbValidator<T> shouldHaveList(@NonNull Condition<List<T>>... conditions) {
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
            throw new AssertionError(errorMessage, error);
        } catch (Exception exception) {
            String errorMessage = String.format("Ошибка при проверке условия %s для [%s]: %s", condition, recordLabel, exception.getMessage());
            throw new RuntimeException(errorMessage, exception);
        }
    }
}
