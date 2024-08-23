package docs.snippets;

import com.codeborne.selenide.Selenide;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.*;

/**
 * Класс содержит примеры использования CSS и XPath селекторов для поиска элементов на странице с помощью Selenide и Selenium.
 */
public class CssXpathSnippets {

    /**
     * Примеры использования CSS и XPath селекторов для поиска элементов на веб-странице.
     */
    void cssXpathExamples() {
        // --- Примеры поиска элемента по атрибуту data-testid ---
        Selenide.$("[data-testid=royal_email]"); // Использование CSS-селектора для поиска элемента по атрибуту data-testid
        Selenide.$(by("data-testid", "royal_email")); // Использование Selenide селектора для поиска по атрибуту data-testid

        // --- Примеры поиска элемента по id ---
        Selenide.$("#email"); // Поиск элемента по id (CSS)
        Selenide.$(byId("email")); // Поиск элемента по id (Selenide)
        Selenide.$(By.id("email")); // Поиск элемента по id (Selenium)
        Selenide.$("[id=email]"); // Поиск элемента по атрибуту id (CSS)
        Selenide.$("input#email"); // Поиск элемента по id и тегу input (CSS)
        Selenide.$x("//*[@id='email']"); // Поиск элемента по id с использованием XPath
        Selenide.$(byXpath("//input[@id='email']")); // Поиск элемента по id с использованием XPath и Selenide

        // --- Примеры поиска элемента по name ---
        Selenide.$("[name='email']"); // Поиск элемента по name (CSS)
        Selenide.$("input[name='email']"); // Поиск элемента по name и тегу input (CSS)
        Selenide.$(by("name", "email")); // Поиск элемента по атрибуту name (Selenide)
        Selenide.$(byName("email")); // Поиск элемента по name (Selenide)

        // --- Примеры поиска элемента по классу ---
        Selenide.$(byClassName("login_form_input_box")); // Поиск элемента по классу (Selenide)
        Selenide.$(".login_form_input_box"); // Поиск элемента по классу (CSS)
        Selenide.$(".input_text.login_form_input_box"); // Поиск элемента по нескольким классам (CSS)
        Selenide.$("input.input_text.login_form_input_box"); // Поиск элемента по нескольким классам и тегу input (CSS)
        Selenide.$x("//*[@class='login_form_input_box']"); // Поиск элемента по классу с использованием XPath

        // --- Примеры поиска элемента внутри другого элемента ---
        // Например, в следующем HTML:
        // <div class="input_text">
        //      <input class="login_form_input_box">
        // </div>
        Selenide.$("div.input_text input.login_form_input_box"); // Поиск элемента по вложенности: класс div и вложенный input с классом (CSS)

        // Пример поиска по атрибуту type
        Selenide.$("[type='password']"); // Поиск элемента по атрибуту type (CSS)
        Selenide.$("input[type='password']"); // Поиск элемента по атрибуту type и тегу input (CSS)
        Selenide.$(by("type", "password")); // Поиск элемента по атрибуту type (Selenide)
        Selenide.$x("//input[@type='password']"); // Поиск элемента по атрибуту type с использованием XPath

        // Пример поиска по частичному совпадению значения атрибута
        Selenide.$("[id^='user']"); // Поиск элемента, id которого начинается с 'user' (CSS)
        Selenide.$("[id$='name']"); // Поиск элемента, id которого заканчивается на 'name' (CSS)
        Selenide.$("[id*='login']"); // Поиск элемента, id которого содержит 'login' (CSS)
        Selenide.$x("//*[contains(@id, 'login')]"); // Поиск элемента, id которого содержит 'login' с использованием XPath

        // Пример поиска по тексту внутри элемента
        Selenide.$x("//*[text()='Войти']"); // Поиск элемента по точному тексту 'Войти' с использованием XPath
        Selenide.$x("//*[contains(text(), 'Зарегистрироваться')]"); // Поиск элемента по частичному совпадению текста с использованием XPath

        // Пример использования псевдоклассов
        Selenide.$("input:first-child"); // Поиск первого дочернего элемента input (CSS)
        Selenide.$("input:last-child"); // Поиск последнего дочернего элемента input (CSS)
        Selenide.$("input:nth-child(2)"); // Поиск второго дочернего элемента input (CSS)

        // Пример поиска элемента по группе классов
        Selenide.$(".class1.class2.class3"); // Поиск элемента, который содержит все указанные классы (CSS)

        // Пример поиска элемента с конкретным порядковым номером среди элементов с одинаковым классом
        Selenide.$(".class:nth-of-type(2)"); // Поиск второго элемента с указанным классом (CSS)

        // Пример использования комбинации селекторов CSS и псевдоклассов
        Selenide.$("div.container > ul > li:first-child"); // Поиск первого элемента списка внутри контейнера (CSS)

        // Пример использования относительных XPath для поиска элементов
        Selenide.$x("//div[@class='container']//li[position()=1]"); // Поиск первого элемента списка внутри контейнера (XPath)

        // Пример использования XPath для поиска элементов на основе их текста с частичным совпадением
        Selenide.$x("//*[contains(text(), 'Частичный текст')]"); // Поиск элемента с текстом, который содержит "Частичный текст" (XPath)

        // Пример поиска по атрибуту aria-label
        Selenide.$("[aria-label='Close']"); // Поиск элемента по атрибуту aria-label (CSS)
        Selenide.$(by("aria-label", "Close")); // Поиск элемента по атрибуту aria-label (Selenide)

        // Пример использования XPath для поиска по нескольким условиям
        Selenide.$x("//input[@type='text' and @name='username']"); // Поиск элемента по нескольким атрибутам (XPath)

        // Пример поиска активного элемента
        Selenide.$(":focus"); // Поиск элемента, который в данный момент находится в фокусе (CSS)

        // Пример поиска всех дочерних элементов внутри конкретного родителя
        Selenide.$("ul > li"); // Поиск всех элементов списка li внутри ul (CSS)

        // Пример поиска элементов по их длине текста
        Selenide.$x("//p[string-length(text()) > 10]"); // Поиск абзацев, длина текста которых больше 10 символов (XPath)

        // Пример поиска элементов по состоянию
        Selenide.$("input:enabled"); // Поиск всех активных (не отключенных) полей ввода (CSS)
        Selenide.$("input:disabled"); // Поиск всех отключенных полей ввода (CSS)
        Selenide.$("input:checked"); // Поиск всех отмеченных чекбоксов или радиокнопок (CSS)

        // Пример работы с динамическими элементами
        Selenide.$("div.dynamic-content:visible"); // Поиск видимого динамического элемента (CSS)
        Selenide.$x("//div[@class='dynamic-content' and not(contains(@style, 'display: none'))]"); // Поиск видимого динамического элемента с использованием XPath

        // Пример использования XPath для поиска элементов с определённым количеством атрибутов
        Selenide.$x("//*[count(@*)=3]"); // Поиск элементов, у которых ровно три атрибута (XPath)
    }
}
