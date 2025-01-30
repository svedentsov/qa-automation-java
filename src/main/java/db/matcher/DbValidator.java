package db.matcher;

import db.matcher.condition.Condition;
import db.matcher.condition.Conditions;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import java.util.Collections;
import java.util.List;

/**
 * Класс для валидации одной или нескольких сущностей базы данных.
 *
 * @param <T> тип сущности
 */
@Slf4j
public class DbValidator<T> {

    private final T singleEntity;
    private final List<T> multipleEntities;

    /**
     * Конструктор для одной сущности.
     *
     * @param entity сущность для проверки
     */
    public DbValidator(@NonNull T entity) {
        Assertions.assertThat(entity)
                .as("Сущность не должна быть null")
                .isNotNull();
        this.singleEntity = entity;
        this.multipleEntities = null;
    }

    /**
     * Конструктор для списка сущностей.
     *
     * @param entities список сущностей для проверки
     */
    public DbValidator(@NonNull List<T> entities) {
        Assertions.assertThat(entities)
                .as("Список сущностей не должен быть null или пустым")
                .isNotNull()
                .isNotEmpty();
        this.singleEntity = null;
        this.multipleEntities = entities;
    }

    /**
     * Проверяет, соответствует ли (соответствуют ли) сущность(и) указанному условию Condition<T>.
     *
     * @param condition условие
     * @return текущий DbValidator
     */
    public DbValidator<T> shouldHave(@NonNull Condition<T> condition) {
        if (singleEntity != null) {
            log.debug("Проверка условия '{}' для одной сущности", condition);
            executeCheck(() -> condition.check(singleEntity), condition, "singleEntity");
        } else {
            // список
            log.debug("Проверка условия '{}' для {} сущностей", condition, multipleEntities.size());
            for (T entity : multipleEntities) {
                executeCheck(() -> condition.check(entity), condition, entity.toString());
            }
        }
        return this;
    }

    /**
     * Проверяет, соответствует ли список сущностей условию Conditions<T>.
     *
     * @param conditions условие для списка
     * @return текущий DbValidator
     */
    public DbValidator<T> shouldHave(@NonNull Conditions<T> conditions) {
        if (multipleEntities == null) {
            // Если вызвали на одну сущность, обернём её в список
            log.debug("Проверка условий '{}' для одиночной сущности", conditions);
            executeCheck(() -> conditions.check(Collections.singletonList(singleEntity)), conditions, "singleEntity");
        } else {
            log.debug("Проверка условий '{}' для {} сущностей", conditions, multipleEntities.size());
            executeCheck(() -> conditions.check(multipleEntities), conditions, "multipleEntities");
        }
        return this;
    }

    private void executeCheck(Runnable check, Object condition, String entityKey) {
        try {
            check.run();
        } catch (AssertionError e) {
            String message = String.format("Условие %s не выполнено для [%s]: %s", condition, entityKey, e.getMessage());
            log.error(message);
            throw new AssertionError(message, e);
        } catch (Exception e) {
            String message = String.format("Ошибка при проверке условия %s для [%s]: %s", condition, entityKey, e.getMessage());
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }
}
