package docs.snippets;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

/**
 * Класс содержит примеры использования CSS и XPath селекторов для поиска элементов на странице с помощью Selenide и Selenium.
 */
public class CssXpathSnippets {

    /**
     * Примеры использования CSS и XPath селекторов для поиска элементов на веб-странице.
     */
    void cssXpathExamples() {
        // --- Примеры поиска элемента по атрибуту data-testid ---
        $("[data-testid=royal_email]"); // Использование CSS-селектора для поиска элемента по атрибуту data-testid
        $(by("data-testid", "royal_email")); // Использование Selenide селектора для поиска по атрибуту data-testid

        // --- Примеры поиска элемента по id ---
        $("#email"); // Поиск элемента по id (CSS)
        $(byId("email")); // Поиск элемента по id (Selenide)
        $(By.id("email")); // Поиск элемента по id (Selenium)
        $("[id=email]"); // Поиск элемента по атрибуту id (CSS)
        $("input#email"); // Поиск элемента по id и тегу input (CSS)
        $x("//*[@id='email']"); // Поиск элемента по id с использованием XPath
        $(byXpath("//input[@id='email']")); // Поиск элемента по id с использованием XPath и Selenide

        // --- Примеры поиска элемента по name ---
        $("[name='email']"); // Поиск элемента по name (CSS)
        $("input[name='email']"); // Поиск элемента по name и тегу input (CSS)
        $(by("name", "email")); // Поиск элемента по атрибуту name (Selenide)
        $(byName("email")); // Поиск элемента по name (Selenide)

        // --- Примеры поиска элемента по классу ---
        $(byClassName("login_form_input_box")); // Поиск элемента по классу (Selenide)
        $(".login_form_input_box"); // Поиск элемента по классу (CSS)
        $(".input_text.login_form_input_box"); // Поиск элемента по нескольким классам (CSS)
        $("input.input_text.login_form_input_box"); // Поиск элемента по нескольким классам и тегу input (CSS)
        $x("//*[@class='login_form_input_box']"); // Поиск элемента по классу с использованием XPath

        // --- Примеры поиска элемента внутри другого элемента ---
        // Например, в следующем HTML:
        // <div class="input_text">
        //      <input class="login_form_input_box">
        // </div>
        $("div.input_text input.login_form_input_box"); // Поиск элемента по вложенности: класс div и вложенный input с классом (CSS)

        // --- Дополнительные примеры для тренировки ---

        // Пример поиска по атрибуту type
        $("[type='password']"); // Поиск элемента по атрибуту type (CSS)
        $("input[type='password']"); // Поиск элемента по атрибуту type и тегу input (CSS)
        $(by("type", "password")); // Поиск элемента по атрибуту type (Selenide)
        $x("//input[@type='password']"); // Поиск элемента по атрибуту type с использованием XPath

        // Пример поиска по частичному совпадению значения атрибута
        $("[id^='user']"); // Поиск элемента, id которого начинается с 'user' (CSS)
        $("[id$='name']"); // Поиск элемента, id которого заканчивается на 'name' (CSS)
        $("[id*='login']"); // Поиск элемента, id которого содержит 'login' (CSS)
        $x("//*[contains(@id, 'login')]"); // Поиск элемента, id которого содержит 'login' с использованием XPath

        // Пример поиска по тексту внутри элемента
        $x("//*[text()='Войти']"); // Поиск элемента по точному тексту 'Войти' с использованием XPath
        $x("//*[contains(text(), 'Зарегистрироваться')]"); // Поиск элемента по частичному совпадению текста с использованием XPath

        // Пример использования псевдоклассов
        $("input:first-child"); // Поиск первого дочернего элемента input (CSS)
        $("input:last-child"); // Поиск последнего дочернего элемента input (CSS)
        $("input:nth-child(2)"); // Поиск второго дочернего элемента input (CSS)
    }
}
