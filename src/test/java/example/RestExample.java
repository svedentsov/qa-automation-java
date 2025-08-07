package example;

import com.svedentsov.app.petstore.data.DataGenerator;
import com.svedentsov.app.petstore.model.Pet;
import com.svedentsov.matcher.EntityValidator;
import com.svedentsov.rest.helper.RestExecutor;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.svedentsov.matcher.PropertyMatcher.value;
import static com.svedentsov.matcher.assertions.BooleanAssertions.isTrue;
import static com.svedentsov.matcher.assertions.InstantAssertions.instantBefore;
import static com.svedentsov.matcher.assertions.ListAssertions.listCountEqual;
import static com.svedentsov.matcher.assertions.NumberAssertions.*;
import static com.svedentsov.matcher.assertions.PropertyAssertions.propertyEqualsTo;
import static com.svedentsov.matcher.assertions.PropertyAssertions.propertyMatches;
import static com.svedentsov.matcher.assertions.StringAssertions.*;
import static com.svedentsov.rest.helper.RestMatcher.*;

@Epic("Демонстрация API Тестов")
@Feature("Примеры использования нового RestMatcher")
@DisplayName("Примеры тестов для REST API с унифицированными матчерами")
public class RestExample {

    private RestExecutor restExecutor;

    @BeforeEach
    void setUp() {
        restExecutor = new RestExecutor("https://petstore.swagger.io/v2");
    }

    @Test
    @DisplayName("Комплексная проверка успешного ответа (статус, время, заголовки, тело)")
    void comprehensiveSuccessfulResponseValidation() {
        // Подготовка: создаем питомца, чтобы получить реальный ответ
        Pet pet = DataGenerator.generateFullDataPet();
        restExecutor.setBody(pet).post("/pet");
        restExecutor.shouldHave(
                // 1. Проверки статуса
                status(numberEqualTo(200)), // код ответа равен 200
                status(numberInRange(200, 299)), // код ответа находится в диапазоне 2xx (успешные)
                // 2. Проверки времени ответа (в миллисекундах)
                time(numberLessOrEqualTo(3000L)), // время ответа не более 3000 мс
                time(numberGreaterThan(0L)), // время ответа больше 0 мс
                // 3. Проверки заголовков
                header("Content-Type", isNotNull()), // заголовок Content-Type существует
                header("Content-Type", contains("application/json")), // содержит подстроку "application/json"
                header("Content-Type", startsWith("application/")), // начинается с "application/"
                header("Content-Type", endsWith("json")), // заканчивается на "json"
                header("Content-Type", equalsIgnoreCase("application/json")), // равен "application/json" без учета регистра
                header("Content-Type", isNotEmpty()), // значение заголовка не пустое
                header("Server", equalTo("Jetty(9.2.9.v20150224)")), // значение заголовка Server равно указанному
                // 4. Проверки тела ответа (общие)
                body(isNotNull()), // тело ответа не null
                body(isNotBlank()), // тело ответа не пустое и не состоит из пробелов
                body(startsWith("{")), // тело начинается с символа '{'
                body(endsWith("}")), // тело заканчивается на символ '}'
                body(contains("category")), // тело содержит подстроку "category"
                body(containsAll("name", "status")), // тело содержит все указанные подстроки
                body(containsAny("available", "pending", "sold")), // тело содержит хотя бы одну из подстрок
                body(isValidJson()) // тело является валидным JSON
        );
    }

    @Test
    @DisplayName("Проверка полей в JSON-теле ответа с использованием JsonPath")
    void jsonPathValueValidation() {
        // Подготовка: создаем питомца
        Pet pet = DataGenerator.generateFullDataPet();
        restExecutor.setBody(pet).post("/pet");
        restExecutor.shouldHave(
                body("name", equalTo(pet.name())), // извлечение строкового поля "name" и проверка на точное равенство
                body("id", numberEqualTo(pet.id()), Long.class), // извлечение числового поля "id" и проверка, что оно равно ID созданного питомца
                body("tags", listCountEqual(2)), // извлечение поля "tags" (массив) и проверка, что его размер равен 2
                body("category.name", propertyMatches(Matchers.containsString("string"))), // извлечение вложенного свойства "category.name" и проверка через Hamcrest (для демонстрации)
                body("isAvailable", isTrue()), // гипотетический пример для булевого поля
                body("createdAt", instantBefore(Instant.now())), // гипотетический пример для поля с датой (предполагаем, что API возвращает ISO-8601 строку)
                body("status", equalTo(pet.status().name().toLowerCase()))
        );
    }

    @Test
    @DisplayName("Проверка ответа через десериализацию в POJO")
    void validatePetObjectAfterDeserialization() {
        // Подготовка
        Pet petToCreate = DataGenerator.generateFullDataPet();
        restExecutor.setBody(petToCreate).post("/pet");
        // Действие
        Response response = restExecutor.get("/pet/" + petToCreate.id());
        // Десериализация
        Pet actualPet = response.as(Pet.class);
        // Проверки с использованием EntityValidator и PropertyMatcher
        EntityValidator.of(actualPet).shouldHave(
                value(Pet::id, propertyEqualsTo(petToCreate.id())), // ID совпадает
                value(Pet::name, startsWith("string")), // Имя начинается с "string"
                value(Pet::status, propertyEqualsTo(petToCreate.status())), // Статус совпадает
                value(Pet::tags, listCountEqual(2)) // Количество тегов равно 2
        );
    }
}
