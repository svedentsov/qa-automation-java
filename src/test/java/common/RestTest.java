package common;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Класс для REST-тестов, расширяющий базовый тестовый класс и включающий методы установки и завершения тестов.
 */
public abstract class RestTest extends BaseTest {
    /**
     * Метод, выполняемый перед всеми тестами. Может быть использован для настройки окружения тестов.
     */
    @BeforeAll
    public static void setUp() {
    }

    /**
     * Метод, выполняемый после всех тестов. Используется для сохранения свойств окружения в файл.
     */
    @AfterAll
    public static void tearDown() {
        Properties envProp = new Properties();
        envProp.setProperty("github", "https://github.com/svedentsov");
        envProp.setProperty("email", "svedentsov@gmail.com");
        try (OutputStream out = new FileOutputStream("./build/allure-results/environment.properties")) {
            envProp.store(out, "environment.properties");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
