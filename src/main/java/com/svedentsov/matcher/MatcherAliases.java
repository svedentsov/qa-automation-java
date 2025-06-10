package com.svedentsov.matcher;

import com.svedentsov.kafka.helper.KafkaMatcher;
import com.svedentsov.matcher.assertions.rest.BodyAssertions.BodyCondition;
import com.svedentsov.rest.helper.RestMatcher;
import io.restassured.response.Response;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.function.Function;

/**
 * Утилитарный класс для предоставления алиасов методов value из различных матчеров.
 * <p>
 * Этот класс решает проблему конфликта имён методов {@code value} в классах
 * {@link PropertyMatcher}, {@link RestMatcher} и {@link KafkaMatcher},
 * позволяя использовать все три типа матчеров в одном тесте без необходимости
 * указания полных имён классов.
 * </p>
 *
 * <p><b>Пример использования:</b></p>
 * <pre>{@code
 * import static com.svedentsov.matcher.utils.MatcherAliases.*;
 *
 * public class MyTest {
 *     @Test
 *     public void testWithAliases() {
 *         // Проверка свойств сущности
 *         var entityCondition = propertyValue(MyEntity::getName, equalTo("test"));
 *
 *         // Проверка REST ответа
 *         var responseCondition = restValue(Response::asString, contains("success"));
 *
 *         // Проверка Kafka сообщения
 *         var kafkaCondition = kafkaValue(ConsumerRecord::key, equalTo("test-key"));
 *     }
 * }
 * }</pre>
 */
@UtilityClass
public class MatcherAliases {

    /**
     * Алиас для {@link PropertyMatcher#value(Function, Condition)}.
     * <p>
     * Создаёт условие для проверки значения, извлечённого из объекта с помощью геттера.
     * Используется для валидации свойств обычных Java-объектов и сущностей.
     * </p>
     *
     * @param <T>       тип исходного объекта
     * @param <R>       тип значения, возвращаемого геттером
     * @param getter    функция для извлечения значения из объекта (например, {@code MyEntity::getName})
     * @param condition условие для проверки извлечённого значения
     * @return условие для проверки объекта типа T
     * @throws NullPointerException если {@code getter} или {@code condition} равны null
     * @see PropertyMatcher#value(Function, Condition)
     */
    public static <T, R> Condition<T> propertyValue(
            @NonNull Function<? super T, ? extends R> getter,
            @NonNull Condition<? super R> condition) {
        return PropertyMatcher.value(getter, condition);
    }

    /**
     * Алиас для {@link RestMatcher#value(Function, Condition)}.
     * <p>
     * Создаёт условие для проверки значения, извлечённого из REST ответа.
     * Используется для валидации HTTP ответов, полученных через RestAssured.
     * </p>
     *
     * @param <R>       тип значения, возвращаемого геттером
     * @param getter    функция для извлечения значения из Response (например, {@code Response::asString})
     * @param condition условие для проверки извлечённого значения
     * @return условие для проверки тела ответа
     * @throws NullPointerException если {@code getter} или {@code condition} равны null
     * @see RestMatcher#value(Function, Condition)
     */
    public static <R> BodyCondition restValue(
            @NonNull Function<? super Response, ? extends R> getter,
            @NonNull Condition<? super R> condition) {
        return RestMatcher.value(getter, condition);
    }

    /**
     * Алиас для {@link KafkaMatcher#value(Function, Condition)}.
     * <p>
     * Создаёт условие для проверки значения, извлечённого из Kafka сообщения.
     * Используется для валидации сообщений Apache Kafka с типами ключа и значения String.
     * </p>
     *
     * @param <R>       тип значения, возвращаемого геттером
     * @param getter    функция для извлечения значения из ConsumerRecord (например, {@code ConsumerRecord::key})
     * @param condition условие для проверки извлечённого значения
     * @return условие для проверки Kafka сообщения
     * @throws NullPointerException если {@code getter} или {@code condition} равны null
     * @see KafkaMatcher#value(Function, Condition)
     */
    public static <R> Condition<ConsumerRecord<String, String>> kafkaValue(
            @NonNull Function<? super ConsumerRecord<String, String>, ? extends R> getter,
            @NonNull Condition<? super R> condition) {
        return KafkaMatcher.value(getter, condition);
    }
}
