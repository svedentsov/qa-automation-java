package com.svedentsov.matcher;

import com.svedentsov.core.exception.ValidationException;
import com.svedentsov.matcher.assertions.CompositeAssertions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Универсальный валидатор для выполнения набора проверок (условий)
 * для одного или нескольких объектов. Предоставляет fluent-интерфейс.
 *
 * @param <T> тип валидируемого объекта
 */
@Slf4j
@RequiredArgsConstructor
public class EntityValidator<T> {

    /**
     * Список объектов для валидации.
     * Для одного объекта используется список из одного элемента.
     */
    private final List<T> records;

    /**
     * Создаёт валидатор для одного объекта.
     *
     * @param record объект для проверки
     * @param <T>    тип объекта
     * @return новый экземпляр валидатора
     */
    public static <T> EntityValidator<T> of(@NonNull T record) {
        return new EntityValidator<>(Collections.singletonList(record));
    }

    /**
     * Создаёт валидатор для коллекции объектов.
     *
     * @param records коллекция объектов для проверки.
     *                Если коллекция пуста, валидатор будет создан, но не будет выполнять проверки
     *                для отдельных элементов, только для самой коллекции (если применимо).
     * @param <T>     тип объектов в коллекции
     * @return новый экземпляр валидатора
     */
    public static <T> EntityValidator<T> of(@NonNull List<T> records) {
        return new EntityValidator<>(records);
    }

    /**
     * Применяет набор условий к каждому объекту в коллекции.
     * Все условия объединяются логическим "И".
     *
     * @param conditions условия, которые должны быть выполнены
     * @return текущий экземпляр валидатора для цепочки вызовов
     * @throws IllegalArgumentException если {@code conditions} пуст или содержит null-элементы.
     */
    @SafeVarargs
    public final EntityValidator<T> shouldHave(@NonNull Condition<T>... conditions) {
        validateConditions(conditions);
        Condition<T> compositeCondition = CompositeAssertions.and(conditions);
        log.debug("Проверка условия '{}' для {} записей", compositeCondition, records.size());
        records.forEach(record -> executeCheck(
                () -> compositeCondition.check(record),
                compositeCondition,
                () -> String.valueOf(record)));
        return this;
    }

    /**
     * Применяет набор условий ко всей коллекции объектов как к единому целому.
     * Все условия объединяются логическим "И".
     *
     * @param conditions условия, которые должны быть выполнены для коллекции
     * @return текущий экземпляр валидатора для цепочки вызовов
     * @throws IllegalArgumentException если {@code conditions} пуст или содержит null-элементы.
     */
    @SafeVarargs
    public final EntityValidator<T> shouldHaveList(@NonNull Condition<List<T>>... conditions) {
        validateConditions(conditions);
        Condition<List<T>> compositeCondition = CompositeAssertions.and(conditions);
        log.debug("Проверка списка условий '{}' для {} записей", compositeCondition, records.size());
        executeCheck(
                () -> compositeCondition.check(records),
                compositeCondition,
                () -> "список записей из " + records.size() + " элементов");
        return this;
    }

    /**
     * Выполняет проверку и оборачивает возможные исключения в ValidationException
     * для стандартизации ошибок валидации.
     *
     * @param check               проверка в виде {@link Runnable}
     * @param condition           проверяемое условие для логгирования
     * @param targetLabelSupplier поставщик описания цели валидации (конкретный объект или список)
     */
    private void executeCheck(Runnable check, Object condition, Supplier<String> targetLabelSupplier) {
        try {
            check.run();
        } catch (AssertionError error) {
            String errorMessage = String.format("Условие '%s' не выполнено для '%s': %s",
                    condition, targetLabelSupplier.get(), error.getMessage());
            throw new ValidationException(errorMessage, error);
        } catch (Exception exception) {
            String errorMessage = String.format("Ошибка при проверке условия '%s' для '%s': %s",
                    condition, targetLabelSupplier.get(), exception.getMessage());
            throw new ValidationException(errorMessage, exception);
        }
    }

    /**
     * Проверяет, что массив условий корректен (не пуст и не содержит null-элементов).
     *
     * @param conditions массив условий
     * @throws IllegalArgumentException если массив пуст или содержит null-элементы.
     */
    private void validateConditions(Object[] conditions) {
        if (conditions.length == 0) {
            throw new IllegalArgumentException("Необходимо передать хотя бы одно условие.");
        }
        if (Arrays.stream(conditions).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Массив условий не может содержать null-элементы.");
        }
    }
}
