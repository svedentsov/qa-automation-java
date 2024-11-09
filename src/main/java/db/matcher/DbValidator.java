package db.matcher;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Класс для валидации одной или нескольких сущностей базы данных.
 *
 * @param <T> тип сущности
 */
@Slf4j
public class DbValidator<T> {

    private final T entity;
    private final List<T> entities;

    /**
     * Конструктор для одной сущности.
     *
     * @param entity сущность для валидации
     */
    public DbValidator(@NonNull T entity) {
        Assertions.assertThat(entity)
                .as("Сущность не должна быть null")
                .isNotNull();
        this.entity = entity;
        this.entities = null;
    }

    /**
     * Конструктор для списка сущностей.
     *
     * @param entities список сущностей для валидации
     */
    public DbValidator(@NonNull List<T> entities) {
        Assertions.assertThat(entities)
                .as("Список сущностей не должен быть null или пустым")
                .isNotNull()
                .isNotEmpty();
        this.entity = null;
        this.entities = entities;
    }

    /**
     * Проверяет, соответствует ли сущность или сущности указанному условию.
     *
     * @param condition условие для проверки
     * @return текущий экземпляр Validator
     */
    public DbValidator<T> shouldHave(@NonNull Condition<T> condition) {
        log.debug("Проверка условия '{}' для сущности", condition);
        condition.check(entity);
        return this;
    }

    /**
     * Проверяет, соответствуют ли сущности указанному условию.
     *
     * @param conditions условие для проверки
     * @return текущий экземпляр Validator
     */
    public DbValidator<T> shouldHave(@NonNull Conditions<T> conditions) {
        log.debug("Проверка условия '{}' для {} сущностей", conditions, entities.size());
        conditions.check(entities);
        return this;
    }
}
