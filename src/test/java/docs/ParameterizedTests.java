package docs;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

/**
 * Класс для параметризованных тестов с использованием библиотеки Selenide.
 */
public class ParameterizedTests {

    /**
     * Метод, выполняемый перед каждым тестом.
     * Открывает главную страницу Яндекса.
     */
    @BeforeEach
    void precondition() {
        Selenide.open("https://ya.ru/");
    }

    /**
     * Метод, выполняемый после каждого теста.
     * Закрывает браузер.
     */
    @AfterEach
    void closeBrowser() {
        Selenide.closeWebDriver();
    }

    /**
     * Параметризованный тест, проверяющий отображение результатов поиска в Яндексе для заданных строк.
     *
     * @param testData поисковый запрос
     */
    @ValueSource(strings = {"Selenide", "JUnit 5"})
    @ParameterizedTest(name = "Проверка отображения поисковых результатов в Яндексе для запроса '{0}'")
    void commonSearchTest(String testData) {
        Selenide.$("#text").setValue(testData);
        Selenide.$("button[type='submit']").click();
        Selenide.$$("li.serp-item").find(text(testData)).shouldBe(visible);
    }

    /**
     * Параметризованный тест с использованием CSV-источника данных, проверяющий отображение результатов поиска в Яндексе.
     *
     * @param testData     поисковый запрос
     * @param expectedText ожидаемый текст в результатах поиска
     */
    @CsvSource(value = {
            "Selenide| concise UI tests in Java",
            "JUnit 5| IntelliJ IDEA"
    }, delimiter = '|')
    @ParameterizedTest(name = "Проверка отображения поисковых результатов в Яндексе для запроса '{0}'")
    void complexSearchTest(String testData, String expectedText) {
        Selenide.$("#text").setValue(testData);
        Selenide.$("button[type='submit']").click();
        Selenide.$$("li.serp-item").find(text(expectedText)).shouldBe(visible);
    }

    /**
     * Метод, предоставляющий тестовые данные для параметризованного теста.
     *
     * @return поток аргументов
     */
    static Stream<Arguments> mixedArgumentsTestDataProvider() {
        return Stream.of(
                Arguments.of("Selenide", List.of(1, 2, 4), true),
                Arguments.of("JUnit 5", List.of(5, 6, 7), false)
        );
    }

    /**
     * Параметризованный тест с использованием различных типов аргументов.
     *
     * @param firstArg      строковый аргумент
     * @param secondArg     список целых чисел
     * @param aBooleanValue булевый аргумент
     */
    @MethodSource(value = "mixedArgumentsTestDataProvider")
    @ParameterizedTest(name = "Тест с разными типами аргументов [{index}]")
    void mixedArgumentsTest(String firstArg, List<Integer> secondArg, boolean aBooleanValue) {
        System.out.println("String:" + firstArg + " list: " + secondArg.toString() + " boolean: " + aBooleanValue);
    }

    /**
     * Параметризованный тест с использованием CSV-источника данных, проверяющий отображение результатов поиска в Яндексе.
     *
     * @param name    имя
     * @param surname фамилия
     */
    @CsvSource(value = {
            "Дмитрий| Тучс",
            "Иван | Иванов"
    }, delimiter = '|')
    @ParameterizedTest(name = "Проверка отображения поисковых результатов в Яндексе для запроса '{0}'")
    void threeInputsTest(String name, String surname) {
        Selenide.$("#surname").setValue(name);
        Selenide.$("#name").setValue(surname);
        Selenide.$("button[type='submit']").click();
    }
}
