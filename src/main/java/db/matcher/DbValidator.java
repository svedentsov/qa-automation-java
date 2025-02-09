package db.matcher;

import db.matcher.assertions.CompositeAssertions;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * Класс для валидации одной или нескольких сущностей с применением заданных условий.
 * Все сущности объединяются в один список, что позволяет единообразно применять проверки.
 *
 * @param <T> тип сущности
 */
@Slf4j
public final class DbValidator<T> {

    /**
     * Список сущностей для валидации.
     */
    private final List<T> entities;

    /**
     * Конструктор для валидации единственной сущности.
     *
     * @param entity сущность для валидации
     */
    public DbValidator(@NonNull T entity) {
        this(Collections.singletonList(entity));
    }

    /**
     * Конструктор для валидации списка сущностей.
     *
     * @param entities список сущностей для валидации
     */
    public DbValidator(@NonNull List<T> entities) {
        this.entities = entities;
    }

    /**
     * Применяет переданные условия проверки ко всем сущностям.
     * Все условия объединяются с помощью логической операции И (AND).
     *
     * @param conditions набор условий проверки сущностей
     * @return текущий экземпляр DbValidator для дальнейшей цепочки вызовов
     */
    @SafeVarargs
    public final DbValidator<T> shouldHave(@NonNull Condition<T>... conditions) {
        Condition<T> compositeCondition = CompositeAssertions.and(conditions);
        log.debug("Проверка условия '{}' для {} сущностей", compositeCondition, entities.size());
        executeCheck(() -> compositeCondition.checkAll(entities), compositeCondition, "entities");
        return this;
    }

    /**
     * Выполняет проверку и обрабатывает исключения, возникающие при выполнении условия.
     *
     * @param check       проверка, представленная в виде {@code Runnable}
     * @param condition   условие, которое проверяется (для формирования сообщения об ошибке)
     * @param entityLabel идентификатор или описание сущности (или группы сущностей)
     */
    private void executeCheck(Runnable check, Object condition, String entityLabel) {
        try {
            check.run();
        } catch (AssertionError error) {
            String errorMessage = String.format("Условие %s не выполнено для [%s]: %s",
                    condition, entityLabel, error.getMessage());
            throw new AssertionError(errorMessage, error);
        } catch (Exception exception) {
            String errorMessage = String.format("Ошибка при проверке условия %s для [%s]: %s",
                    condition, entityLabel, exception.getMessage());
            throw new RuntimeException(errorMessage, exception);
        }
    }
}
