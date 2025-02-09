package db.matcher.assertions;

import db.matcher.Condition;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

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
    public static <T> Condition<T> equalsTo(Object expectedValue) {
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
    public static <T> Condition<T> isNull() {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть null")
                .isNull();
    }

    /**
     * Возвращает условие, проверяющее, что значение свойства (строка или коллекция) пустое.
     *
     * @param <T> тип проверяемого свойства
     * @return условие проверки пустоты значения
     * @throws IllegalArgumentException если значение не является строкой или коллекцией
     */
    public static <T> Condition<T> propertyIsEmpty() {
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
            } else {
                throw new IllegalArgumentException("Значение не является строкой или коллекцией");
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
    public static <T> Condition<T> isOfType(Class<?> expectedType) {
        return value -> Assertions.assertThat(value.getClass())
                .as("Значение должно быть типа %s", expectedType.getName())
                .isEqualTo(expectedType);
    }

    /**
     * Возвращает условие, проверяющее, что значение является экземпляром (или подклассом) указанного типа.
     *
     * @param type ожидаемый класс или интерфейс
     * @param <T>  тип проверяемого свойства
     * @return условие проверки принадлежности к типу
     */
    public static <T> Condition<T> isInstanceOf(Class<?> type) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть экземпляром %s", type.getName())
                .isInstanceOf(type);
    }

    /**
     * Возвращает условие, проверяющее, что значение является подклассом или реализует указанный тип.
     *
     * @param expectedSuperType ожидаемый суперкласс или интерфейс
     * @param <T>               тип проверяемого свойства
     * @return условие проверки принадлежности к типу
     */
    public static <T> Condition<T> isAssignableFrom(Class<?> expectedSuperType) {
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
    public static <T> Condition<T> allPropertiesEqual(Map<Function<T, ?>, Object> expectedProperties) {
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
    public static <T> Condition<T> in(List<?> values) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно входить в список %s", values)
                .isIn(values);
    }

    /**
     * Возвращает условие, проверяющее, что значение больше указанного.
     *
     * @param lowerBound нижняя граница (исключительно)
     * @param <T>        тип проверяемого свойства, реализующего Comparable
     * @return условие проверки, что значение больше нижней границы
     */
    public static <T extends Comparable<T>> Condition<T> greaterThan(T lowerBound) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть больше %s", lowerBound)
                .isGreaterThan(lowerBound);
    }

    /**
     * Возвращает условие, проверяющее, что значение меньше указанного.
     *
     * @param upperBound верхняя граница (исключительно)
     * @param <T>        тип проверяемого свойства, реализующего Comparable
     * @return условие проверки, что значение меньше верхней границы
     */
    public static <T extends Comparable<T>> Condition<T> lessThan(T upperBound) {
        return value -> Assertions.assertThat(value)
                .as("Значение должно быть меньше %s", upperBound)
                .isLessThan(upperBound);
    }

    /**
     * Возвращает условие, проверяющее, что значение находится в диапазоне между lowerBound и upperBound (включительно).
     *
     * @param lowerBound нижняя граница диапазона
     * @param upperBound верхняя граница диапазона
     * @param <T>        тип проверяемого свойства, реализующего Comparable
     * @return условие проверки, что значение находится в заданном диапазоне
     */
    public static <T extends Comparable<T>> Condition<T> between(T lowerBound, T upperBound) {
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
    public static <T> Condition<T> toStringEquals(String expected) {
        return value -> Assertions.assertThat(value.toString())
                .as("Строковое представление должно быть равно %s", expected)
                .isEqualTo(expected);
    }

    /**
     * Возвращает условие, проверяющее, что строка содержит заданную подстроку.
     *
     * @param substring подстрока, которую должна содержать строка
     * @return условие проверки наличия подстроки
     */
    public static Condition<String> containsSubstring(String substring) {
        return value -> Assertions.assertThat(value)
                .as("Строка должна содержать подстроку %s", substring)
                .contains(substring);
    }

    /**
     * Возвращает условие, позволяющее задать произвольную проверку через Consumer.
     *
     * @param consumer проверка в виде Consumer, которая выбрасывает исключение, если условие не выполнено
     * @param <T>      тип проверяемого свойства
     * @return условие, удовлетворяющее переданной проверке
     */
    public static <T> Condition<T> satisfies(Consumer<T> consumer) {
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
    public static <T> Condition<T> matchesPredicate(Function<T, Boolean> predicate, String description) {
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
    public static <K, V> Condition<Map<K, V>> mapContainsKey(K key) {
        return map -> Assertions.assertThat(map)
                .as("Карта должна содержать ключ %s", key)
                .containsKey(key);
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
    public static <K, V> Condition<Map<K, V>> mapContainsEntry(K key, V value) {
        return map -> Assertions.assertThat(map)
                .as("Карта должна содержать запись [%s=%s]", key, value)
                .containsEntry(key, value);
    }

    /**
     * Проверяет, что строка имеет длину, равную ожидаемой.
     */
    public static Condition<String> hasLength(int expectedLength) {
        return value -> Assertions.assertThat(value.length())
                .as("Строка должна иметь длину %d", expectedLength)
                .isEqualTo(expectedLength);
    }

    /**
     * Проверяет, что значение (список, коллекция или массив) отсортировано в естественном порядке.
     */
    public static <T> Condition<T> propertyIsSorted() {
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
    public static <T> Condition<T> hasSize(int expectedSize) {
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
     * Проверяет, что строка состоит только из пробельных символов или пуста.
     */
    public static Condition<String> isBlank() {
        return value -> Assertions.assertThat(value)
                .as("Строка должна быть пустой или состоять только из пробелов")
                .isBlank();
    }

    /**
     * Проверяет, что строковое представление значения начинается с заданного префикса.
     */
    public static <T> Condition<T> toStringStartsWith(String prefix) {
        return value -> Assertions.assertThat(value.toString())
                .as("Строковое представление должно начинаться с %s", prefix)
                .startsWith(prefix);
    }

    /**
     * Проверяет, что строковое представление значения заканчивается заданным суффиксом.
     */
    public static <T> Condition<T> toStringEndsWith(String suffix) {
        return value -> Assertions.assertThat(value.toString())
                .as("Строковое представление должно заканчиваться на %s", suffix)
                .endsWith(suffix);
    }

    /**
     * Возвращает условие для проверки того, что карта содержит заданное значение.
     *
     * @param value значение, которое должна содержать карта
     * @param <K>   тип ключей в карте
     * @param <V>   тип значений в карте
     * @return условие проверки наличия значения в карте
     */
    public static <K, V> Condition<Map<K, V>> mapContainsValue(V value) {
        return map -> Assertions.assertThat(map)
                .as("Карта должна содержать значение %s", value)
                .containsValue(value);
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
    public static <T, E> Condition<T> everyElementSatisfies(Function<T, ? extends Iterable<E>> extractor, Consumer<E> elementConsumer) {
        return entity -> {
            Iterable<E> elements = extractor.apply(entity);
            for (E element : elements) {
                elementConsumer.accept(element);
            }
        };
    }
}
