package db.matcher;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

/**
 * Класс для валидации одной сущности базы данных.
 *
 * @param <T> тип сущности
 */
@Slf4j
public class ValidateEntity<T> {

    private final T entity;

    /**
     * Конструктор ValidateEntity.
     *
     * @param entity сущность для валидации
     * @throws IllegalArgumentException если сущность null
     */
    public ValidateEntity(@NonNull T entity) {
        Assertions.assertThat(entity)
                .as("Сущность не должна быть null")
                .isNotNull();
        this.entity = entity;
    }

    /**
     * Проверяет, соответствует ли сущность указанному условию.
     *
     * @param condition условие для проверки
     * @return текущий экземпляр ValidateEntity
     * @throws Exception если происходит ошибка при проверке условия
     */
    public ValidateEntity<T> shouldHave(@NonNull Condition<T> condition) throws Exception {
        log.debug("Проверка условия '{}' для сущности", condition);
        try {
            condition.check(entity);
        } catch (Exception e) {
            log.error("Ошибка при проверке условия: {}", e.getMessage(), e);
            throw e;
        }
        return this;
    }
}
