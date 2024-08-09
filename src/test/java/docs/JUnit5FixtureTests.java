package docs;

import org.junit.jupiter.api.*;

import static io.qameta.allure.Allure.step;

/**
 * Класс для демонстрации использования фикстур JUnit 5 с использованием Allure.
 */
public class JUnit5FixtureTests {

    /**
     * Метод, выполняемый перед всеми тестами.
     */
    @BeforeAll
    public static void beforeAll() {
        step("Этот метод выполняется перед всеми тестами");
    }

    /**
     * Метод, выполняемый перед каждым тестом.
     */
    @BeforeEach
    public void beforeEach() {
        step("Этот метод выполняется перед каждым тестом");
    }

    /**
     * Первый тест с использованием Allure для фикстур.
     */
    @Test
    public void firstAllureFixtureTest() {
        step("Шаг внутри firstAllureFixtureTest");
        System.out.println("Это первый тест");
        Assertions.assertTrue(true);
    }

    /**
     * Второй тест с использованием Allure для фикстур.
     */
    @Test
    public void secondAllureFixtureTest() {
        step("Шаг внутри secondAllureFixtureTest");
        System.out.println("Это второй тест");
        Assertions.assertTrue(true);
    }

    /**
     * Метод, выполняемый после каждого теста.
     */
    @AfterEach
    public void afterEach() {
        step("Этот метод выполняется после каждого теста");
    }

    /**
     * Метод, выполняемый после всех тестов.
     */
    @AfterAll
    public static void afterAll() {
        step("Этот метод выполняется после всех тестов");
    }
}
