package db.matcher;

import db.matcher.condition.Condition;
import db.matcher.condition.Conditions;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

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
     * @param entity проверяемая сущность; не должна быть null
     */
    public DbValidator(@NonNull T entity) {
        this.entities = Collections.singletonList(entity);
    }

    /**
     * Конструктор для валидации списка сущностей.
     *
     * @param entities список проверяемых сущностей; не должен быть null или пустым
     * @throws AssertionError если список сущностей пуст
     */
    public DbValidator(@NonNull List<T> entities) {
        Assertions.assertThat(entities)
                .as("Список сущностей не должен быть пустым")
                .isNotEmpty();
        this.entities = entities;
    }

    /**
     * Применяет к каждой сущности из списка заданное условие.
     *
     * @param condition условие для проверки
     * @return текущий экземпляр DbValidator для построения цепочки вызовов
     * @throws AssertionError   если условие не выполнено для хотя бы одной сущности
     * @throws RuntimeException если происходит другая ошибка при проверке
     */
    public DbValidator<T> shouldHave(@NonNull Condition<T> condition) {
        log.debug("Проверка условия '{}' для {} сущностей", condition, entities.size());
        entities.forEach(entity -> runCheck(() -> condition.check(entity), condition, entity.toString()));
        return this;
    }

    /**
     * Применяет заданный набор условий к списку сущностей.
     *
     * @param conditions условия для проверки списка сущностей
     * @return текущий экземпляр DbValidator для построения цепочки вызовов
     * @throws AssertionError   если условие не выполнено
     * @throws RuntimeException если происходит другая ошибка при проверке
     */
    public DbValidator<T> shouldHave(@NonNull Conditions<T> conditions) {
        log.debug("Проверка условий '{}' для {} сущностей", conditions, entities.size());
        runCheck(() -> conditions.check(entities), conditions, "entities");
        return this;
    }

    /**
     * Выполняет проверку и обрабатывает исключения, возникающие при выполнении условия.
     *
     * @param check       проверка, представленная в виде {@code Runnable}
     * @param condition   условие, которое проверяется (для формирования сообщения об ошибке)
     * @param entityLabel идентификатор или описание сущности (или группы сущностей)
     */
    private void runCheck(Runnable check, Object condition, String entityLabel) {
        try {
            check.run();
        } catch (AssertionError e) {
            String errorMessage = String.format("Условие %s не выполнено для [%s]: %s", condition, entityLabel, e.getMessage());
            log.error(errorMessage);
            throw new AssertionError(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Ошибка при проверке условия %s для [%s]: %s", condition, entityLabel, e.getMessage());
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }
}
