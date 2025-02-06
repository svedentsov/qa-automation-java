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
     */
    public DbValidator(@NonNull T entity) {
        this(Collections.singletonList(entity));
    }

    /**
     * Конструктор для валидации списка сущностей.
     */
    public DbValidator(@NonNull List<T> entities) {
        this.entities = entities;
    }

    /**
     * Применяет переданные проверщики (Checker) ко всем сущностям.
     * Все проверки объединяются с помощью логической операции И (and).
     *
     * @param checkers набор проверщиков для проверки сущностей
     * @return текущий экземпляр DbValidator для построения цепочки вызовов
     */
    @SafeVarargs
    public final DbValidator<T> shouldHave(@NonNull Checker<T>... checkers) {
        // Создаём составной Checker с помощью логической операции "И"
        Checker<T> composite = CompositeAssertions.and(checkers);
        log.debug("Проверка условия '{}' для {} сущностей", composite, entities.size());
        runCheck(() -> composite.checkAll(entities), composite, "entities");
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
