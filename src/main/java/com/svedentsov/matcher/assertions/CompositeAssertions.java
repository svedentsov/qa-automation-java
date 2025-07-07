package com.svedentsov.matcher.assertions;

import com.svedentsov.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.*;

/**
 * Утилитный класс для композиционных (логических) операций над проверками: AND, OR, NOT, и др.
 */
@UtilityClass
public class CompositeAssertions {
    /**
     * Возвращает проверку, которая проходит, если <strong>все</strong> переданные условия выполняются.
     * Использует "короткое замыкание": останавливается на первом же неуспешном условии.
     *
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие AND
     */
    @SafeVarargs
    public static <T> Condition<T> and(Condition<T>... conditions) {
        validateConditions(conditions, "AND");
        return entity -> {
            for (int i = 0; i < conditions.length; i++) {
                try {
                    conditions[i].check(entity);
                } catch (AssertionError e) {
                    String failureMessage = String.format("AND-проверка провалилась на условии #%d из %d.%n--> Исходная ошибка: %s",
                            i + 1, conditions.length, e.getMessage());
                    throw new AssertionError(failureMessage, e);
                }
            }
        };
    }

    /**
     * Возвращает проверку, которая проходит, если <strong>хотя бы одно</strong> из переданных условий выполняется.
     * Использует "короткое замыкание": останавливается после первого успешного условия.
     *
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие OR
     */
    @SafeVarargs
    public static <T> Condition<T> or(Condition<T>... conditions) {
        validateConditions(conditions, "OR");
        return entity -> {
            List<ConditionResult> failedResults = new ArrayList<>();
            for (Condition<T> condition : conditions) {
                Optional<AssertionError> error = checkCondition(condition, entity);
                if (error.isEmpty()) {
                    return; // Успех, выходим досрочно
                }
                failedResults.add(new ConditionResult(condition, error.get()));
            }
            String report = formatConditionResults("Ни одно из OR-условий не выполнено", failedResults);
            throw new AssertionError(report);
        };
    }

    /**
     * Возвращает проверку, которая проходит, если <strong>ни одно</strong> из переданных условий не выполняется.
     * Является синонимом для {@code exactlyNOf(0, conditions)}.
     *
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие NOT
     */
    @SafeVarargs
    public static <T> Condition<T> not(Condition<T>... conditions) {
        return exactlyNOf(0, conditions);
    }

    /**
     * Возвращает проверку, которая проходит, если <strong>хотя бы n</strong> из переданных условий выполняются.
     *
     * @param n          минимальное число успешных условий (n > 0)
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие nOf
     */
    @SafeVarargs
    public static <T> Condition<T> nOf(int n, Condition<T>... conditions) {
        validateCountArgs(n, conditions, "nOf", 1); // n должно быть >= 1
        return entity -> {
            List<ConditionResult> results = checkAllConditions(entity, conditions);
            long passedCount = results.stream().filter(ConditionResult::isSuccess).count();
            Assertions.assertThat(passedCount)
                    .withFailMessage(() -> {
                        String summary = String.format("Ожидалось, что выполнится минимум %d усл., но выполнилось только %d.", n, passedCount);
                        return formatConditionResults(summary, results);
                    })
                    .isGreaterThanOrEqualTo(n);
        };
    }

    /**
     * Возвращает проверку, которая проходит, если <strong>ровно n</strong> из переданных условий выполняются.
     *
     * @param n          точное число успешных условий (n >= 0)
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие exactlyNOf
     */
    @SafeVarargs
    public static <T> Condition<T> exactlyNOf(int n, Condition<T>... conditions) {
        validateCountArgs(n, conditions, "exactlyNOf", 0); // n должно быть >= 0
        return entity -> {
            List<ConditionResult> results = checkAllConditions(entity, conditions);
            long passedCount = results.stream().filter(ConditionResult::isSuccess).count();
            Assertions.assertThat(passedCount)
                    .withFailMessage(() -> {
                        String summary = String.format("Ожидалось, что выполнится ровно %d усл., но выполнилось %d.", n, passedCount);
                        return formatConditionResults(summary, results);
                    })
                    .isEqualTo(n);
        };
    }

    /**
     * Возвращает проверку, которая проходит, если <strong>не более n</strong> из переданных условий выполняются.
     *
     * @param n          максимальное число успешных условий (n >= 0)
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие atMostNOf
     */
    @SafeVarargs
    public static <T> Condition<T> atMostNOf(int n, Condition<T>... conditions) {
        validateCountArgs(n, conditions, "atMostNOf", 0); // n должно быть >= 0
        return entity -> {
            int passedCount = 0;
            List<ConditionResult> results = new ArrayList<>();
            for (Condition<T> condition : conditions) {
                ConditionResult result = new ConditionResult(condition, checkCondition(condition, entity).orElse(null));
                results.add(result);
                if (result.isSuccess()) {
                    passedCount++;
                }
                if (passedCount > n) {
                    String summary = String.format("Ожидалось, что выполнится не более %d усл., но уже выполнилось %d.", n, passedCount);
                    throw new AssertionError(formatConditionResults(summary, results));
                }
            }
        };
    }

    /**
     * Возвращает проверку, которая проходит, если <strong>ровно одно</strong> из переданных условий выполняется.
     *
     * @param conditions набор условий; не может быть null или пустым
     * @param <T>        тип сущности
     * @return составное условие XOR
     */
    @SafeVarargs
    public static <T> Condition<T> xor(Condition<T>... conditions) {
        return exactlyNOf(1, conditions);
    }


    /**
     * Проверяет массив условий на null, пустоту и наличие null-элементов.
     */
    private static <T> void validateConditions(Condition<T>[] conditions, String operationName) {
        requireNonNull(conditions, () -> operationName + ": массив условий не может быть null");
        if (conditions.length == 0) {
            throw new IllegalArgumentException(operationName + ": требует хотя бы одного условия");
        }
        Stream.of(conditions).forEach(cond ->
                requireNonNull(cond, operationName + ": элемент массива условий не может быть null")
        );
    }

    /**
     * Проверяет корректность аргументов для методов с подсчётом 'n'.
     */
    private static <T> void validateCountArgs(int n, Condition<T>[] conditions, String operationName, int minN) {
        validateConditions(conditions, operationName);
        if (n < minN) {
            throw new IllegalArgumentException(String.format("%s: n должно быть >= %d, но было %d", operationName, minN, n));
        }
        if (n > conditions.length) {
            throw new IllegalArgumentException(
                    String.format("%s: n (%d) не может быть больше количества условий (%d)", operationName, n, conditions.length)
            );
        }
    }

    /**
     * Безопасно выполняет одно условие и возвращает Optional с ошибкой, если она произошла.
     *
     * @return {@code Optional.empty()} при успехе, или {@code Optional.of(AssertionError)} при провале.
     */
    private static <T> Optional<AssertionError> checkCondition(Condition<T> condition, T entity) {
        try {
            condition.check(entity);
            return Optional.empty();
        } catch (AssertionError e) {
            return Optional.of(e);
        }
    }

    /**
     * Выполняет все условия для данной сущности и собирает результаты.
     */
    private static <T> List<ConditionResult> checkAllConditions(T entity, Condition<T>[] conditions) {
        return Arrays.stream(conditions)
                .map(cond -> new ConditionResult(cond, checkCondition(cond, entity).orElse(null)))
                .collect(Collectors.toList());
    }

    /**
     * Форматирует список результатов проверок в читаемый многострочный отчет.
     *
     * @param summary заголовок отчета
     * @param results список результатов для форматирования
     * @return отформатированная строка
     */
    private static String formatConditionResults(String summary, List<ConditionResult> results) {
        StringBuilder report = new StringBuilder(summary);
        report.append(String.format("%n%nДетальный отчет по условиям (%d шт.):%n", results.size()));

        for (int i = 0; i < results.size(); i++) {
            ConditionResult result = results.get(i);
            report.append(String.format("  [%d] ", i + 1));
            if (result.isSuccess()) {
                report.append("✓ УСПЕШНО: ");
                report.append(result.condition.toString());
            } else {
                report.append("✗ ПРОВАЛЕНО: ");
                report.append(result.condition.toString());
                String errorMessage = result.error.getMessage().replace("\n", "\n    |           ");
                report.append(String.format("%n    |--> Ошибка: %s", errorMessage));
            }
            report.append(System.lineSeparator());
        }
        return report.toString();
    }

    /**
     * Внутренний класс для хранения результата выполнения одного условия.
     */
    private static class ConditionResult {
        final Condition<?> condition;
        final AssertionError error; // null, если проверка успешна

        ConditionResult(Condition<?> condition, AssertionError error) {
            this.condition = requireNonNull(condition);
            this.error = error;
        }

        boolean isSuccess() {
            return error == null;
        }
    }
}
