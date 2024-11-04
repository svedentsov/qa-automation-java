package db.matcher;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Класс для валидации списка сущностей базы данных.
 *
 * @param <T> тип сущности
 */
@Slf4j
public class ValidateEntities<T> {

    private final List<T> entities;

    /**
     * Конструктор ValidateEntities.
     *
     * @param entities список сущностей для валидации
     * @throws IllegalArgumentException если список сущностей null или пуст
     */
    public ValidateEntities(@NonNull List<T> entities) {
        Assertions.assertThat(entities)
                .as("Список сущностей не должен быть null или пустым")
                .isNotNull()
                .isNotEmpty();
        this.entities = entities;
    }

    /**
     * Проверяет, соответствуют ли сущности указанному условию.
     *
     * @param condition условие для проверки
     * @return текущий экземпляр ValidateEntities
     * @throws Exception если происходит ошибка при проверке условий
     */
    public ValidateEntities<T> shouldHave(@NonNull Conditions<T> condition) throws Exception {
        log.debug("Проверка условия '{}' для {} сущностей", condition, entities.size());
        try {
            condition.check(entities);
        } catch (Exception e) {
            log.error("Ошибка при проверке условий: {}", e.getMessage(), e);
            throw e;
        }
        return this;
    }
}
