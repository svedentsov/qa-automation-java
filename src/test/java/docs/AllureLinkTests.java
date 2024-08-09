package docs;

import io.qameta.allure.Allure;
import io.qameta.allure.Link;
import io.qameta.allure.Links;
import org.junit.jupiter.api.Test;

/**
 * Класс для тестирования ссылок в Allure.
 */
public class AllureLinkTests {

    /**
     * Тест с использованием статической ссылки.
     */
    @Test
    @Link(name = "Статическая ссылка", url = "https://qameta.io")
    public void staticLinkTest() {
    }

    /**
     * Тест с использованием нескольких статических ссылок.
     */
    @Test
    @Links({
            @Link(name = "Первая статическая ссылка", url = "https://qameta.io"),
            @Link(name = "Вторая статическая ссылка", url = "https://qameta.io")
    })
    public void staticLinksTest() {
    }

    /**
     * Тест с использованием динамической ссылки, задаваемой внутри теста.
     */
    @Test
    public void dynamicLinkTest() {
        Allure.link("Динамическая ссылка", "https://qameta.io");
    }
}
