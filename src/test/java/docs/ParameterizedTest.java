package docs;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static io.qameta.allure.Allure.step;

/**
 * Класс для параметризованных тестов с использованием библиотеки Selenide.
 */
public class ParameterizedTest {

    /**
     * Общий тест поиска в Яндексе для различных запросов.
     *
     * @param query поисковый запрос
     */
    @org.junit.jupiter.params.ParameterizedTest(name = "Проверка отображения поисковых результатов в Яндексе для запроса '{0}'")
    @ValueSource(strings = {"Selenide", "JUnit 5"})
    void commonSearchTest(String query) {
        Selenide.open("https://ya.ru/");
        performYaSearch(query);
        step("Проверка наличия результата с текстом: " + query, () -> {
            Selenide.$$("li.serp-item").findBy(text(query)).shouldBe(visible);
        });
    }

    /**
     * Комплексный тест поиска в Яндексе с проверкой ожидаемого текста.
     *
     * @param query        поисковый запрос
     * @param expectedText ожидаемый текст в результатах поиска
     */
    @org.junit.jupiter.params.ParameterizedTest(name = "Проверка поиска в Яндексе для запроса '{0}' и ожидаемого текста '{1}'")
    @CsvSource(value = {
            "Selenide|concise UI tests in Java",
            "JUnit 5|IntelliJ IDEA"
    }, delimiter = '|')
    void complexSearchTest(String query, String expectedText) {
        Selenide.open("https://ya.ru/");
        performYaSearch(query);
        step("Проверка наличия результата с текстом: " + expectedText, () -> {
            Selenide.$$("li.serp-item").findBy(text(expectedText)).shouldBe(visible);
        });
    }

    /**
     * Тест авторизации с различными учетными данными.
     *
     * @param username имя пользователя
     * @param password пароль
     * @param expected ожидаемый результат авторизации
     */
    @org.junit.jupiter.params.ParameterizedTest(name = "Авторизация с пользователем: {0}")
    @CsvSource({
            "user1, pass1, true",
            "user2, wrongpass, false"
    })
    void loginTest(String username, String password, boolean expected) {
        Selenide.open("https://example.com/login");
        step("Ввод имени пользователя: " + username, () -> {
            Selenide.$("#username").setValue(username);
        });
        step("Ввод пароля", () -> {
            Selenide.$("#password").setValue(password).pressEnter();
        });
        if (expected) {
            step("Проверка успешной авторизации", () -> {
                Selenide.$("#logout").shouldBe(visible);
            });
        } else {
            step("Проверка сообщения об ошибке", () -> {
                Selenide.$("#error").shouldHave(text("Неверные учетные данные"));
            });
        }
    }

    /**
     * Тест с разными типами данных.
     *
     * @param name     имя элемента
     * @param numbers  список чисел
     * @param isActive состояние активности
     */
    @org.junit.jupiter.params.ParameterizedTest(name = "Тест с элементом {0}")
    @MethodSource("mixedDataProvider")
    void mixedDataTest(String name, List<Integer> numbers, boolean isActive) {
        step("Проверка элемента: " + name);
        System.out.println("Имя элемента: " + name + ", Числа: " + numbers + ", Активен: " + isActive);
    }

    /**
     * Метод, предоставляющий данные для mixedDataTest.
     *
     * @return поток аргументов
     */
    static Stream<Arguments> mixedDataProvider() {
        return Stream.of(
                Arguments.of("Item1", List.of(1, 2, 3), true),
                Arguments.of("Item2", List.of(4, 5, 6), false)
        );
    }

    private void performYaSearch(String query) {
        step("Ввод поискового запроса: " + query, () -> {
            Selenide.$("#text").setValue(query);
        });
        step("Нажатие кнопки поиска", () -> {
            Selenide.$("button[type='submit']").click();
        });
    }
}
