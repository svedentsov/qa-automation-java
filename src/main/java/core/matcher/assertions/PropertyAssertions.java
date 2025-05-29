package core.matcher.assertions;

import core.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.HamcrestCondition;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Утилитный класс для проверки различных свойств сущности: равенство, null, принадлежность к списку, тип, сравнения и т.д.
 */
@UtilityClass
public class PropertyAssertions {

    /**
     * Функциональный интерфейс для проверки отдельного свойства.
     *
     * @param <V> тип проверяемого свойства
     */
    @FunctionalInterface
    public interface PropertyCondition<V> extends Condition<V> {
    }

    /**
     * Возвращает условие, проверяющее, что значение свойства равно ожидаемому.
     *
     * @param expectedValue ожидаемое значение свойства
     * @param <T>           тип проверяемого свойства
     * @return условие проверки равенства
     */
    public static <T> PropertyCondition<T> equalsTo(Object expectedValue) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть равно %s", expectedValue)
                .isEqualTo(expectedValue);
    }

    /**
     * Возвращает условие, проверяющее, что значение свойства равно null.
     *
     * @param <T> тип проверяемого свойства
     * @return условие проверки, что значение равно null
     */
    public static <T> PropertyCondition<T> isNull() {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть null")
                .isNull();
    }

    /**
     * Возвращает условие, проверяющее, что значение свойства не равно null.
     *
     * @param <T> тип проверяемого свойства
     * @return условие проверки, что значение не равно null
     */
    public static <T> PropertyCondition<T> isNotNull() {
        return value -> Assertions.assertThat(value)
                .as("Значение не должно быть null")
                .isNotNull();
    }

    /**
     * Возвращает условие, проверяющее, что значение свойства (строка или коллекция) пустое.
     *
     * @param <T> тип проверяемого свойства
     * @return условие проверки пустоты значения
     * @throws IllegalArgumentException если значение не является строкой или коллекцией
     */
    public static <T> PropertyCondition<T> propertyIsEmpty() {
        return value -> {
            Assertions.assertThat(value)
                    .as("Значение не должно быть null")
                    .isNotNull();
            if (value instanceof String) {
                Assertions.assertThat((String) value)
                        .as("Строка должна быть пустой")
                        .isEmpty();
            } else if (value instanceof Collection) {
                Assertions.assertThat((Collection<?>) value)
                        .as("Коллекция должна быть пустой")
                        .isEmpty();
            } else if (value instanceof Map) {
                Assertions.assertThat((Map<?, ?>) value)
                        .as("Карта должна быть пустой")
                        .isEmpty();
            } else {
                throw new IllegalArgumentException("Значение не является строкой, коллекцией или картой");
            }
        };
    }

    /**
     * Возвращает условие, проверяющее, что значение свойства (строка, коллекция или карта) не пустое.
     *
     * @param <T> тип проверяемого свойства
     * @return условие проверки непустоты значения
     * @throws IllegalArgumentException если значение не является строкой, коллекцией или картой
     */
    public static <T> PropertyCondition<T> propertyIsNotEmpty() {
        return value -> {
            Assertions.assertThat(value)
                    .as("Значение не должно быть null")
                    .isNotNull();
            if (value instanceof String) {
                Assertions.assertThat((String) value)
                        .as("Строка не должна быть пустой")
                        .isNotEmpty();
            } else if (value instanceof Collection) {
                Assertions.assertThat((Collection<?>) value)
                        .as("Коллекция не должна быть пустой")
                        .isNotEmpty();
            } else if (value instanceof Map) {
                Assertions.assertThat((Map<?, ?>) value)
                        .as("Карта не должна быть пустой")
                        .isNotEmpty();
            } else if (value.getClass().isArray()) {
                Assertions.assertThat(((Object[]) value).length)
                        .as("Массив не должен быть пустым")
                        .isGreaterThan(0);
            } else {
                throw new IllegalArgumentException("Значение не является строкой, коллекцией, картой или массивом");
            }
        };
    }

    /**
     * Возвращает условие, проверяющее, что значение имеет точный тип {@code expectedType}.
     *
     * @param expectedType ожидаемый тип значения
     * @param <T>          тип проверяемого свойства
     * @return условие проверки типа (строгое сравнение классов)
     */
    public static <T> PropertyCondition<T> isOfType(Class<?> expectedType) {
        return value -> Assertions.assertThat(value.getClass())
                .as("Значение должно быть типа %s", expectedType.getName())
                .isEqualTo(expectedType);
    }

    /**
     * Возвращает условие, проверяющее, что значение не имеет точный тип {@code expectedType}.
     *
     * @param expectedType ожидаемый тип значения
     * @param <T>          тип проверяемого свойства
     * @return условие проверки типа (строгое сравнение классов)
     */
    public static <T> PropertyCondition<T> isNotOfType(Class<?> expectedType) {
        return value -> Assertions.assertThat(value.getClass())
                .as("Значение не должно быть типа %s", expectedType.getName())
                .isNotEqualTo(expectedType);
    }

    /**
     * Возвращает условие, проверяющее, что значение является экземпляром (или подклассом) указанного типа.
     *
     * @param type ожидаемый класс или интерфейс
     * @param <T>  тип проверяемого свойства
     * @return условие проверки принадлежности к типу
     */
    public static <T> PropertyCondition<T> isInstanceOf(Class<?> type) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть экземпляром %s", type.getName())
                .isInstanceOf(type);
    }

    /**
     * Возвращает условие, проверяющее, что значение не является экземпляром (или подклассом) указанного типа.
     *
     * @param type ожидаемый класс или интерфейс
     * @param <T>  тип проверяемого свойства
     * @return условие проверки принадлежности к типу
     */
    public static <T> PropertyCondition<T> isNotInstanceOf(Class<?> type) {
        return value -> Assertions.assertThat(value)
                .as("Значение не должно быть экземпляром %s", type.getName())
                .isNotInstanceOf(type);
    }

    /**
     * Возвращает условие, проверяющее, что значение является подклассом или реализует указанный тип.
     *
     * @param expectedSuperType ожидаемый суперкласс или интерфейс
     * @param <T>               тип проверяемого свойства
     * @return условие проверки принадлежности к типу
     */
    public static <T> PropertyCondition<T> isAssignableFrom(Class<?> expectedSuperType) {
        return value -> Assertions.assertThat(expectedSuperType.isAssignableFrom(value.getClass()))
                .as("Значение должно быть подклассом/реализовывать %s", expectedSuperType.getName())
                .isTrue();
    }

    /**
     * Возвращает условие, проверяющее, что все указанные свойства сущности равны ожидаемым значениям.
     *
     * @param expectedProperties карта, где ключ – функция-геттер свойства, а значение – ожидаемое значение
     * @param <T>                тип сущности
     * @return условие проверки нескольких свойств
     */
    public static <T> PropertyCondition<T> allPropertiesEqual(Map<Function<T, ?>, Object> expectedProperties) {
        return value -> {
            for (Map.Entry<Function<T, ?>, Object> entry : expectedProperties.entrySet()) {
                Function<T, ?> getter = entry.getKey();
                Object expectedValue = entry.getValue();
                Object actualValue = getter.apply(value);
                Assertions.assertThat(actualValue)
                        .as("Проверка, что значение равно %s", expectedValue)
                        .isEqualTo(expectedValue);
            }
        };
    }

    /**
     * Возвращает условие, проверяющее, что значение входит в заданный список.
     *
     * @param values список допустимых значений
     * @param <T>    тип проверяемого свойства
     * @return условие проверки принадлежности значения списку
     */
    public static <T> PropertyCondition<T> in(List<?> values) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно входить в список %s", values)
                .isIn(values);
    }

    /**
     * Возвращает условие, проверяющее, что значение не входит в заданный список.
     *
     * @param values список недопустимых значений
     * @param <T>    тип проверяемого свойства
     * @return условие проверки отсутствия значения в списке
     */
    public static <T> PropertyCondition<T> notIn(List<?> values) {
        return value -> Assertions.assertThat(value)
                .as("Значение не должно входить в список %s", values)
                .isNotIn(values);
    }

    /**
     * Возвращает условие, проверяющее, что значение больше указанного.
     *
     * @param lowerBound нижняя граница (исключительно)
     * @param <T>        тип проверяемого свойства, реализующего Comparable
     * @return условие проверки, что значение больше нижней границы
     */
    public static <T extends Comparable<T>> PropertyCondition<T> greaterThan(T lowerBound) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть больше %s", lowerBound)
                .isGreaterThan(lowerBound);
    }

    /**
     * Возвращает условие, проверяющее, что значение больше или равно указанному.
     *
     * @param lowerBound нижняя граница
     * @param <T>        тип проверяемого свойства, реализующего Comparable
     * @return условие проверки, что значение больше или равно нижней границе
     */
    public static <T extends Comparable<T>> PropertyCondition<T> greaterThanOrEqualTo(T lowerBound) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть больше или равно %s", lowerBound)
                .isGreaterThanOrEqualTo(lowerBound);
    }

    /**
     * Возвращает условие, проверяющее, что значение меньше указанного.
     *
     * @param upperBound верхняя граница (исключительно)
     * @param <T>        тип проверяемого свойства, реализующего Comparable
     * @return условие проверки, что значение меньше верхней границы
     */
    public static <T extends Comparable<T>> PropertyCondition<T> lessThan(T upperBound) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть меньше %s", upperBound)
                .isLessThan(upperBound);
    }

    /**
     * Возвращает условие, проверяющее, что значение меньше или равно указанному.
     *
     * @param upperBound верхняя граница
     * @param <T>        тип проверяемого свойства, реализующего Comparable
     * @return условие проверки, что значение меньше или равно верхней границы
     */
    public static <T extends Comparable<T>> PropertyCondition<T> lessThanOrEqualTo(T upperBound) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть меньше или равно %s", upperBound)
                .isLessThanOrEqualTo(upperBound);
    }

    /**
     * Возвращает условие, проверяющее, что значение находится в диапазоне между lowerBound и upperBound (включительно).
     *
     * @param lowerBound нижняя граница диапазона
     * @param upperBound верхняя граница диапазона
     * @param <T>        тип проверяемого свойства, реализующего Comparable
     * @return условие проверки, что значение находится в заданном диапазоне
     */
    public static <T extends Comparable<T>> PropertyCondition<T> between(T lowerBound, T upperBound) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть между %s и %s", lowerBound, upperBound)
                .isBetween(lowerBound, upperBound);
    }

    /**
     * Возвращает условие, проверяющее, что строковое представление значения равно ожидаемому.
     *
     * @param expected ожидаемое строковое представление
     * @param <T>      тип проверяемого свойства
     * @return условие проверки строкового представления
     */
    public static <T> PropertyCondition<T> toStringEquals(String expected) {
        return value -> Assertions.assertThat(value.toString())
                .as("Строковое представление должно быть равно %s", expected)
                .isEqualTo(expected);
    }

    /**
     * Возвращает условие, проверяющее, что строковое представление значения начинается с заданного префикса.
     *
     * @param prefix префикс, с которого должно начинаться строковое представление
     * @param <T>    тип проверяемого свойства
     * @return условие проверки начала строкового представления
     */
    public static <T> PropertyCondition<T> toStringStartsWith(String prefix) {
        return value -> Assertions.assertThat(value.toString())
                .as("Строковое представление должно начинаться с %s", prefix)
                .startsWith(prefix);
    }

    /**
     * Возвращает условие, проверяющее, что строковое представление значения заканчивается заданным суффиксом.
     *
     * @param suffix суффикс, которым должно заканчиваться строковое представление
     * @param <T>    тип проверяемого свойства
     * @return условие проверки конца строкового представления
     */
    public static <T> PropertyCondition<T> toStringEndsWith(String suffix) {
        return value -> Assertions.assertThat(value.toString())
                .as("Строковое представление должно заканчиваться на %s", suffix)
                .endsWith(suffix);
    }

    /**
     * Возвращает условие, проверяющее, что строковое представление значения соответствует заданному регулярному выражению.
     *
     * @param regex регулярное выражение
     * @param <T>   тип проверяемого свойства
     * @return условие проверки соответствия строкового представления регулярному выражению
     */
    public static <T> PropertyCondition<T> toStringMatchesRegex(String regex) {
        return value -> Assertions.assertThat(value.toString())
                .as("Строковое представление должно соответствовать рег. выражению %s", regex)
                .matches(regex);
    }

    /**
     * Возвращает условие, проверяющее, что строка содержит заданную подстроку.
     *
     * @param substring подстрока, которую должна содержать строка
     * @return условие проверки наличия подстроки
     */
    public static PropertyCondition<String> containsSubstring(String substring) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать подстроку %s", substring)
                .contains(substring);
    }

    /**
     * Возвращает условие, проверяющее, что строка содержит заданную подстроку, игнорируя регистр.
     *
     * @param substring подстрока, которую должна содержать строка (регистр игнорируется)
     * @return условие проверки наличия подстроки без учета регистра
     */
    public static PropertyCondition<String> containsSubstringIgnoringCase(String substring) {
        return value -> Assertions.assertThat(value.toLowerCase())
                .as("Строка должна содержать подстроку '%s' (без учета регистра)", substring)
                .contains(substring.toLowerCase());
    }

    /**
     * Возвращает условие, позволяющее задать произвольную проверку через Consumer.
     *
     * @param consumer проверка в виде Consumer, которая выбрасывает исключение, если условие не выполнено
     * @param <T>      тип проверяемого свойства
     * @return условие, удовлетворяющее переданной проверке
     */
    public static <T> PropertyCondition<T> satisfies(Consumer<T> consumer) {
        return value -> consumer.accept(value);
    }

    /**
     * Возвращает условие, проверяющее, что значение удовлетворяет заданному предикату.
     *
     * @param predicate   функция-предикат для проверки значения
     * @param description описание условия (для сообщения об ошибке)
     * @param <T>         тип проверяемого свойства
     * @return условие, проверяющее, что значение удовлетворяет предикату
     */
    public static <T> PropertyCondition<T> matchesPredicate(Function<T, Boolean> predicate, String description) {
        return value -> Assertions.assertThat(predicate.apply(value))
                .as("Значение должно удовлетворять условию: %s", description)
                .isTrue();
    }

    /**
     * Возвращает условие для проверки того, что карта содержит заданный ключ.
     *
     * @param key ключ, который должна содержать карта
     * @param <K> тип ключей в карте
     * @param <V> тип значений в карте
     * @return условие проверки наличия ключа в карте
     */
    public static <K, V> PropertyCondition<Map<K, V>> mapContainsKey(K key) {
        return map -> Assertions.assertThat(map)
                .as("Карта должна содержать ключ %s", key)
                .containsKey(key);
    }

    /**
     * Возвращает условие для проверки того, что карта не содержит заданный ключ.
     *
     * @param key ключ, который не должна содержать карта
     * @param <K> тип ключей в карте
     * @param <V> тип значений в карте
     * @return условие проверки отсутствия ключа в карте
     */
    public static <K, V> PropertyCondition<Map<K, V>> mapDoesNotContainKey(K key) {
        return map -> Assertions.assertThat(map)
                .as("Карта не должна содержать ключ %s", key)
                .doesNotContainKey(key);
    }

    /**
     * Возвращает условие для проверки того, что карта содержит указанную запись (ключ и значение).
     *
     * @param key   ключ записи
     * @param value значение записи
     * @param <K>   тип ключей в карте
     * @param <V>   тип значений в карте
     * @return условие проверки наличия записи в карте
     */
    public static <K, V> PropertyCondition<Map<K, V>> mapContainsEntry(K key, V value) {
        return map -> Assertions.assertThat(map)
                .as("Карта должна содержать запись [%s=%s]", key, value)
                .containsEntry(key, value);
    }

    /**
     * Возвращает условие для проверки того, что карта содержит заданное значение.
     *
     * @param value значение, которое должна содержать карта
     * @param <K>   тип ключей в карте
     * @param <V>   тип значений в карте
     * @return условие проверки наличия значения в карте
     */
    public static <K, V> PropertyCondition<Map<K, V>> mapContainsValue(V value) {
        return map -> Assertions.assertThat(map)
                .as("Карта должна содержать значение %s", value)
                .containsValue(value);
    }

    /**
     * Возвращает условие для проверки того, что карта не содержит заданное значение.
     *
     * @param value значение, которое не должна содержать карта
     * @param <K>   тип ключей в карте
     * @param <V>   тип значений в карте
     * @return условие проверки отсутствия значения в карте
     */
    public static <K, V> PropertyCondition<Map<K, V>> mapDoesNotContainValue(V value) {
        return map -> Assertions.assertThat(map)
                .as("Карта не должна содержать значение %s", value)
                .doesNotContainValue(value);
    }

    /**
     * Возвращает условие для проверки того, что карта пуста.
     *
     * @param <K> тип ключей в карте
     * @param <V> тип значений в карте
     * @return условие проверки, что карта пуста
     */
    public static <K, V> PropertyCondition<Map<K, V>> mapIsEmpty() {
        return map -> Assertions.assertThat(map)
                .as("Карта должна быть пустой")
                .isEmpty();
    }

    /**
     * Возвращает условие для проверки того, что карта не пуста.
     *
     * @param <K> тип ключей в карте
     * @param <V> тип значений в карте
     * @return условие проверки, что карта не пуста
     */
    public static <K, V> PropertyCondition<Map<K, V>> mapIsNotEmpty() {
        return map -> Assertions.assertThat(map)
                .as("Карта не должна быть пустой")
                .isNotEmpty();
    }

    /**
     * Проверяет, что строка имеет длину, равную ожидаемой.
     */
    public static PropertyCondition<String> hasLength(int expectedLength) {
        return value -> Assertions.assertThat(value.length())
                .as("Строка должна иметь длину %d", expectedLength)
                .isEqualTo(expectedLength);
    }

    /**
     * Проверяет, что значение (список, коллекция или массив) отсортировано в естественном порядке.
     */
    public static <T> PropertyCondition<T> propertyIsSorted() {
        return value -> {
            Assertions.assertThat(value)
                    .as("Значение не должно быть null")
                    .isNotNull();
            if (value instanceof List) {
                Assertions.assertThat((List<?>) value)
                        .as("Список должен быть отсортирован")
                        .isSorted();
            } else if (value instanceof Collection) {
                List<?> list = List.copyOf((Collection<?>) value);
                Assertions.assertThat(list)
                        .as("Коллекция должна быть отсортирована")
                        .isSorted();
            } else if (value.getClass().isArray()) {
                Assertions.assertThat((Object[]) value)
                        .as("Массив должен быть отсортирован")
                        .isSorted();
            } else {
                throw new IllegalArgumentException("Значение не является коллекцией или массивом");
            }
        };
    }

    /**
     * Проверяет, что размер значения (строка, коллекция, массив или карта) равен ожидаемому.
     */
    public static <T> PropertyCondition<T> hasSize(int expectedSize) {
        return value -> {
            Assertions.assertThat(value)
                    .as("Значение не должно быть null")
                    .isNotNull();
            if (value instanceof Collection) {
                Assertions.assertThat(((Collection<?>) value).size())
                        .as("Размер коллекции должен быть %d", expectedSize)
                        .isEqualTo(expectedSize);
            } else if (value.getClass().isArray()) {
                Assertions.assertThat(((Object[]) value).length)
                        .as("Размер массива должен быть %d", expectedSize)
                        .isEqualTo(expectedSize);
            } else if (value instanceof String) {
                Assertions.assertThat(((String) value).length())
                        .as("Длина строки должна быть %d", expectedSize)
                        .isEqualTo(expectedSize);
            } else if (value instanceof Map) {
                Assertions.assertThat(((Map<?, ?>) value).size())
                        .as("Размер карты должен быть %d", expectedSize)
                        .isEqualTo(expectedSize);
            } else {
                throw new IllegalArgumentException("Невозможно определить размер для типа " + value.getClass());
            }
        };
    }

    /**
     * Проверяет, что размер коллекции, массива, строки или карты больше указанного значения.
     */
    public static <T> PropertyCondition<T> propertyHasSizeGreaterThan(int minSize) {
        return value -> {
            Assertions.assertThat(value)
                    .as("Значение не должно быть null")
                    .isNotNull();
            if (value instanceof Collection) {
                Assertions.assertThat(((Collection<?>) value).size())
                        .as("Размер коллекции должен быть больше %d", minSize)
                        .isGreaterThan(minSize);
            } else if (value.getClass().isArray()) {
                Assertions.assertThat(((Object[]) value).length)
                        .as("Размер массива должен быть больше %d", minSize)
                        .isGreaterThan(minSize);
            } else if (value instanceof String) {
                Assertions.assertThat(((String) value).length())
                        .as("Длина строки должна быть больше %d", minSize)
                        .isGreaterThan(minSize);
            } else if (value instanceof Map) {
                Assertions.assertThat(((Map<?, ?>) value).size())
                        .as("Размер карты должен быть больше %d", minSize)
                        .isGreaterThan(minSize);
            } else {
                throw new IllegalArgumentException("Невозможно определить размер для типа " + value.getClass());
            }
        };
    }

    /**
     * Проверяет, что размер коллекции, массива, строки или карты меньше указанного значения.
     */
    public static <T> PropertyCondition<T> propertyHasSizeLessThan(int maxSize) {
        return value -> {
            Assertions.assertThat(value)
                    .as("Значение не должно быть null")
                    .isNotNull();
            if (value instanceof Collection) {
                Assertions.assertThat(((Collection<?>) value).size())
                        .as("Размер коллекции должен быть меньше %d", maxSize)
                        .isLessThan(maxSize);
            } else if (value.getClass().isArray()) {
                Assertions.assertThat(((Object[]) value).length)
                        .as("Размер массива должен быть меньше %d", maxSize)
                        .isLessThan(maxSize);
            } else if (value instanceof String) {
                Assertions.assertThat(((String) value).length())
                        .as("Длина строки должна быть меньше %d", maxSize)
                        .isLessThan(maxSize);
            } else if (value instanceof Map) {
                Assertions.assertThat(((Map<?, ?>) value).size())
                        .as("Размер карты должен быть меньше %d", maxSize)
                        .isLessThan(maxSize);
            } else {
                throw new IllegalArgumentException("Невозможно определить размер для типа " + value.getClass());
            }
        };
    }

    /**
     * Проверяет, что строка состоит только из пробельных символов или пуста.
     */
    public static PropertyCondition<String> isBlank() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть пустой или состоять только из пробелов")
                .isBlank();
    }

    /**
     * Проверяет, что строка не состоит только из пробельных символов и не пуста.
     */
    public static PropertyCondition<String> isNotBlank() {
        return value -> Assertions.assertThat(value)
                .as("Строка не должна быть пустой или состоять только из пробелов")
                .isNotBlank();
    }

    /**
     * Возвращает условие для проверки того, что каждый элемент коллекции (извлечённый с помощью getter'а)
     * удовлетворяет заданной проверке.
     *
     * @param extractor       функция для извлечения коллекции из сущности
     * @param elementConsumer проверка для каждого элемента коллекции
     * @param <T>             тип сущности
     * @param <E>             тип элементов коллекции
     * @return условие проверки всех элементов коллекции
     */
    public static <T, E> PropertyCondition<T> everyElementSatisfies(Function<T, ? extends Iterable<E>> extractor, Consumer<E> elementConsumer) {
        return entity -> {
            Iterable<E> elements = extractor.apply(entity);
            for (E element : elements) {
                elementConsumer.accept(element);
            }
        };
    }

    /**
     * Возвращает условие для проверки того, что каждый элемент коллекции удовлетворяет заданному предикату.
     *
     * @param predicate   функция-предикат для проверки каждого элемента
     * @param description описание условия (для сообщения об ошибке)
     * @param <E>         тип элементов коллекции
     * @return условие проверки всех элементов коллекции
     */
    public static <E> PropertyCondition<Collection<E>> allElementsMatchPredicate(Function<E, Boolean> predicate, String description) {
        return collection -> {
            for (E element : collection) {
                Assertions.assertThat(predicate.apply(element))
                        .as("Каждый элемент должен удовлетворять условию: %s. Не удовлетворил: %s", description, element)
                        .isTrue();
            }
        };
    }

    /**
     * Возвращает условие для проверки того, что хотя бы один элемент коллекции удовлетворяет заданному предикату.
     *
     * @param predicate   функция-предикат для проверки элемента
     * @param description описание условия (для сообщения об ошибке)
     * @param <E>         тип элементов коллекции
     * @return условие проверки наличия хотя бы одного элемента, удовлетворяющего предикату
     */
    public static <E> PropertyCondition<Collection<E>> anyElementMatchesPredicate(Function<E, Boolean> predicate, String description) {
        return collection -> {
            boolean matches = false;
            for (E element : collection) {
                if (predicate.apply(element)) {
                    matches = true;
                    break;
                }
            }
            Assertions.assertThat(matches)
                    .as("Хотя бы один элемент должен удовлетворять условию: %s", description)
                    .isTrue();
        };
    }

    /**
     * Возвращает условие для проверки того, что коллекция содержит заданный элемент.
     *
     * @param element элемент, который должна содержать коллекция
     * @param <E>     тип элементов коллекции
     * @return условие проверки наличия элемента в коллекции
     */
    public static <E> PropertyCondition<Collection<E>> collectionContains(E element) {
        return collection -> Assertions.assertThat(collection)
                .as("Коллекция должна содержать элемент %s", element)
                .contains(element);
    }

    /**
     * Проверяет, что строковое представление значения соответствует заданному регулярному выражению, игнорируя регистр.
     *
     * @param regex регулярное выражение
     * @param <T>   тип проверяемого свойства
     * @return условие проверки соответствия строкового представления регулярному выражению без учета регистра
     */
    public static <T> PropertyCondition<T> stringMatchesRegexIgnoringCase(String regex) {
        return value -> Assertions.assertThat(value.toString())
                .as("Строковое представление должно соответствовать рег. выражению '%s' (без учета регистра)", regex)
                .matches(Pattern.compile(regex, Pattern.CASE_INSENSITIVE).asPredicate());
    }

    /**
     * Проверяет, что строка равна другой строке, игнорируя регистр.
     *
     * @param expected ожидаемая строка
     * @return условие проверки равенства строк без учета регистра
     */
    public static PropertyCondition<String> equalsIgnoreCase(String expected) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть равна '%s' (без учета регистра)", expected)
                .isEqualToIgnoringCase(expected);
    }

    /**
     * Проверяет, что коллекция содержит только уникальные элементы.
     *
     * @param <E> тип элементов коллекции
     * @return условие проверки уникальности элементов коллекции
     */
    public static <E> PropertyCondition<Collection<E>> collectionHasUniqueElements() {
        return collection -> Assertions.assertThat(collection)
                .as("Коллекция должна содержать только уникальные элементы")
                .doesNotHaveDuplicates();
    }

    /**
     * Возвращает условие, проверяющее, что значение свойства соответствует заданному Hamcrest Matcher.
     *
     * @param matcher Matcher для проверки значения
     * @param <T>     тип проверяемого свойства
     * @return условие проверки через HamcrestCondition
     * @throws IllegalArgumentException если matcher == null
     */
    public static <T> PropertyCondition<T> propertyMatches(Matcher<? super T> matcher) {
        Objects.requireNonNull(matcher, "matcher не может быть null");
        return value -> Assertions.assertThat(value)
                .as("Значение должно соответствовать условию %s", matcher)
                .is(new HamcrestCondition<>(matcher));
    }

    /**
     * Возвращает условие, проверяющее, что значение свойства НЕ соответствует заданному Hamcrest Matcher.
     *
     * @param matcher Matcher для проверки значения
     * @param <T>     тип проверяемого свойства
     * @return условие проверки через HamcrestCondition (отрицание)
     * @throws IllegalArgumentException если matcher == null
     */
    public static <T> PropertyCondition<T> propertyDoesNotMatch(Matcher<? super T> matcher) {
        Objects.requireNonNull(matcher, "matcher не может быть null");
        return value -> Assertions.assertThat(value)
                .as("Значение не должно соответствовать условию %s", matcher)
                .isNot(new HamcrestCondition<>(matcher));
    }
}
