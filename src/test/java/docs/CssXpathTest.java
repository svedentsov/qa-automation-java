package docs;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.*;

/**
 * Утилитный класс для поиска веб-элементов с использованием CSS и XPath селекторов.
 */
public class CssXpathTest {
    /**
     * Примеры использования CSS и XPath селекторов для поиска элементов на веб-странице.
     * Этот метод может быть использован для демонстрации различных способов поиска элементов.
     */
    public void cssXpathExamples() {
        // --- Примеры поиска элемента по атрибуту data-testid ---
        SelenideElement element1 = Selenide.$("[data-testid=royal_email]"); // Использование CSS-селектора для поиска элемента по атрибуту data-testid
        SelenideElement element2 = Selenide.$(by("data-testid", "royal_email")); // Использование Selenide селектора для поиска по атрибуту data-testid

        // --- Примеры поиска элемента по id ---
        SelenideElement element3 = Selenide.$("#email"); // Поиск элемента по id (CSS)
        SelenideElement element4 = Selenide.$(byId("email")); // Поиск элемента по id (Selenide)
        SelenideElement element5 = Selenide.$(By.id("email")); // Поиск элемента по id (Selenium)
        SelenideElement element6 = Selenide.$("[id=email]"); // Поиск элемента по атрибуту id (CSS)
        SelenideElement element7 = Selenide.$("input#email"); // Поиск элемента по id и тегу input (CSS)
        SelenideElement element8 = Selenide.$x("//*[@id='email']"); // Поиск элемента по id с использованием XPath
        SelenideElement element9 = Selenide.$(byXpath("//input[@id='email']")); // Поиск элемента по id с использованием XPath и Selenide

        // --- Примеры поиска элемента по name ---
        SelenideElement element10 = Selenide.$("[name='email']"); // Поиск элемента по name (CSS)
        SelenideElement element11 = Selenide.$("input[name='email']"); // Поиск элемента по name и тегу input (CSS)
        SelenideElement element12 = Selenide.$(by("name", "email")); // Поиск элемента по атрибуту name (Selenide)
        SelenideElement element13 = Selenide.$(byName("email")); // Поиск элемента по name (Selenide)

        // --- Примеры поиска элемента по классу ---
        SelenideElement element14 = Selenide.$(byClassName("login_form_input_box")); // Поиск элемента по классу (Selenide)
        SelenideElement element15 = Selenide.$(".login_form_input_box"); // Поиск элемента по классу (CSS)
        SelenideElement element16 = Selenide.$(".input_text.login_form_input_box"); // Поиск элемента по нескольким классам (CSS)
        SelenideElement element17 = Selenide.$("input.input_text.login_form_input_box"); // Поиск элемента по нескольким классам и тегу input (CSS)
        SelenideElement element18 = Selenide.$x("//*[@class='login_form_input_box']"); // Поиск элемента по классу с использованием XPath

        // --- Примеры поиска элемента внутри другого элемента ---
        // Например, в следующем HTML:
        // <div class="input_text">
        //      <input class="login_form_input_box">
        // </div>
        SelenideElement element19 = Selenide.$("div.input_text input.login_form_input_box"); // Поиск элемента по вложенности: класс div и вложенный input с классом (CSS)

        // Пример поиска по атрибуту type
        SelenideElement element20 = Selenide.$("[type='password']"); // Поиск элемента по атрибуту type (CSS)
        SelenideElement element21 = Selenide.$("input[type='password']"); // Поиск элемента по атрибуту type и тегу input (CSS)
        SelenideElement element22 = Selenide.$(by("type", "password")); // Поиск элемента по атрибуту type (Selenide)
        SelenideElement element23 = Selenide.$x("//input[@type='password']"); // Поиск элемента по атрибуту type с использованием XPath

        // Пример поиска по частичному совпадению значения атрибута
        SelenideElement element24 = Selenide.$("[id^='user']"); // Поиск элемента, id которого начинается с 'user' (CSS)
        SelenideElement element25 = Selenide.$("[id$='name']"); // Поиск элемента, id которого заканчивается на 'name' (CSS)
        SelenideElement element26 = Selenide.$("[id*='login']"); // Поиск элемента, id которого содержит 'login' (CSS)
        SelenideElement element27 = Selenide.$x("//*[contains(@id, 'login')]"); // Поиск элемента, id которого содержит 'login' с использованием XPath

        // Пример поиска по тексту внутри элемента
        SelenideElement element28 = Selenide.$x("//*[text()='Войти']"); // Поиск элемента по точному тексту 'Войти' с использованием XPath
        SelenideElement element29 = Selenide.$x("//*[contains(text(), 'Зарегистрироваться')]"); // Поиск элемента по частичному совпадению текста с использованием XPath

        // Пример использования псевдоклассов
        SelenideElement element30 = Selenide.$("input:first-child"); // Поиск первого дочернего элемента input (CSS)
        SelenideElement element31 = Selenide.$("input:last-child"); // Поиск последнего дочернего элемента input (CSS)
        SelenideElement element32 = Selenide.$("input:nth-child(2)"); // Поиск второго дочернего элемента input (CSS)

        // Пример поиска элемента по группе классов
        SelenideElement element33 = Selenide.$(".class1.class2.class3"); // Поиск элемента, который содержит все указанные классы (CSS)

        // Пример поиска элемента с конкретным порядковым номером среди элементов с одинаковым классом
        SelenideElement element34 = Selenide.$(".class:nth-of-type(2)"); // Поиск второго элемента с указанным классом (CSS)

        // Пример использования комбинации селекторов CSS и псевдоклассов
        SelenideElement element35 = Selenide.$("div.container > ul > li:first-child"); // Поиск первого элемента списка внутри контейнера (CSS)

        // Пример использования относительных XPath для поиска элементов
        SelenideElement element36 = Selenide.$x("//div[@class='container']//li[position()=1]"); // Поиск первого элемента списка внутри контейнера (XPath)

        // Пример использования XPath для поиска элементов на основе их текста с частичным совпадением
        SelenideElement element37 = Selenide.$x("//*[contains(text(), 'Частичный текст')]"); // Поиск элемента с текстом, который содержит "Частичный текст" (XPath)

        // Пример поиска по атрибуту aria-label
        SelenideElement element38 = Selenide.$("[aria-label='Close']"); // Поиск элемента по атрибуту aria-label (CSS)
        SelenideElement element39 = Selenide.$(by("aria-label", "Close")); // Поиск элемента по атрибуту aria-label (Selenide)

        // Пример использования XPath для поиска по нескольким условиям
        SelenideElement element40 = Selenide.$x("//input[@type='text' and @name='username']"); // Поиск элемента по нескольким атрибутам (XPath)

        // Пример поиска активного элемента
        SelenideElement element41 = Selenide.$(":focus"); // Поиск элемента, который в данный момент находится в фокусе (CSS)

        // Пример поиска всех дочерних элементов внутри конкретного родителя
        ElementsCollection elements1 = Selenide.$$("ul > li"); // Поиск всех элементов списка li внутри ul (CSS)

        // Пример поиска элементов по их длине текста
        ElementsCollection elements2 = Selenide.$$x("//p[string-length(text()) > 10]"); // Поиск абзацев, длина текста которых больше 10 символов (XPath)

        // Пример поиска элементов по состоянию
        ElementsCollection elements3 = Selenide.$$("input:enabled"); // Поиск всех активных (не отключенных) полей ввода (CSS)
        ElementsCollection elements4 = Selenide.$$("input:disabled"); // Поиск всех отключенных полей ввода (CSS)
        ElementsCollection elements5 = Selenide.$$("input:checked"); // Поиск всех отмеченных чекбоксов или радиокнопок (CSS)

        // Пример работы с динамическими элементами
        SelenideElement element42 = Selenide.$("div.dynamic-content:visible"); // Поиск видимого динамического элемента (CSS)
        SelenideElement element43 = Selenide.$x("//div[@class='dynamic-content' and not(contains(@style, 'display: none'))]"); // Поиск видимого динамического элемента с использованием XPath

        // Пример использования XPath для поиска элементов с определённым количеством атрибутов
        ElementsCollection elements6 = Selenide.$$x("//*[count(@*)=3]"); // Поиск элементов, у которых ровно три атрибута (XPath)
    }

    /**
     * Поиск элемента по атрибуту.
     *
     * @param attributeName  имя атрибута
     * @param attributeValue значение атрибута
     * @return найденный элемент
     */
    public SelenideElement findByAttribute(String attributeName, String attributeValue) {
        return Selenide.$("[" + attributeName + "='" + attributeValue + "']");
    }

    /**
     * Поиск элемента по ID.
     *
     * @param id значение ID
     * @return найденный элемент
     */
    public SelenideElement findById(String id) {
        return Selenide.$("#" + id);
    }

    /**
     * Поиск элемента по имени (name).
     *
     * @param name значение атрибута name
     * @return найденный элемент
     */
    public SelenideElement findByName(String name) {
        return Selenide.$("[name='" + name + "']");
    }

    /**
     * Поиск элемента по классу.
     *
     * @param className имя класса
     * @return найденный элемент
     */
    public SelenideElement findByClass(String className) {
        return Selenide.$("." + className);
    }

    /**
     * Поиск элемента по точному тексту.
     *
     * @param text точный текст элемента
     * @return найденный элемент
     */
    public SelenideElement findByExactText(String text) {
        return Selenide.$(byText(text));
    }

    /**
     * Поиск элемента по частичному тексту.
     *
     * @param partialText часть текста элемента
     * @return найденный элемент
     */
    public SelenideElement findByPartialText(String partialText) {
        return Selenide.$(withText(partialText));
    }

    /**
     * Поиск элемента с использованием псевдоклассов CSS.
     *
     * @param cssSelector селектор CSS
     * @param pseudoClass псевдокласс (например, first-child, last-child)
     * @return найденный элемент
     */
    public SelenideElement findByPseudoClass(String cssSelector, String pseudoClass) {
        return Selenide.$(cssSelector + ":" + pseudoClass);
    }

    /**
     * Поиск элемента по частичному совпадению значения атрибута.
     *
     * @param attributeName  имя атрибута
     * @param attributeValue часть значения атрибута
     * @param matchType      тип совпадения: "^" (начинается с), "$" (заканчивается на), "*" (содержит)
     * @return найденный элемент
     */
    public SelenideElement findByPartialAttribute(String attributeName, String attributeValue, String matchType) {
        return Selenide.$("[" + attributeName + matchType + "='" + attributeValue + "']");
    }

    /**
     * Поиск активного элемента на странице.
     *
     * @return активный элемент
     */
    public SelenideElement findActiveElement() {
        return Selenide.$(":focus");
    }

    /**
     * Поиск элементов по состоянию (enabled, disabled, checked).
     *
     * @param elementTag тег элемента (например, input)
     * @param state      состояние элемента (enabled, disabled, checked)
     * @return коллекция найденных элементов
     */
    public ElementsCollection findByState(String elementTag, String state) {
        return Selenide.$$(elementTag + ":" + state);
    }

    /**
     * Поиск видимых элементов с определенным классом.
     *
     * @param className имя класса
     * @return коллекция найденных элементов
     */
    public ElementsCollection findVisibleElementsByClass(String className) {
        return Selenide.$$("." + className + ":visible");
    }

    /**
     * Поиск элементов с определенным количеством атрибутов.
     *
     * @param attributeCount количество атрибутов
     * @return коллекция найденных элементов
     */
    public ElementsCollection findElementsByAttributeCount(int attributeCount) {
        return Selenide.$$x("//*[count(@*)=" + attributeCount + "]");
    }

    /**
     * Поиск элемента по сложному XPath выражению.
     *
     * @param xpathExpression выражение XPath
     * @return найденный элемент
     */
    public SelenideElement findByComplexXPath(String xpathExpression) {
        return Selenide.$x(xpathExpression);
    }

    /**
     * Поиск элемента по aria-label.
     *
     * @param ariaLabel значение aria-label
     * @return найденный элемент
     */
    public SelenideElement findByAriaLabel(String ariaLabel) {
        return Selenide.$("[aria-label='" + ariaLabel + "']");
    }

    /**
     * Поиск элемента внутри другого элемента.
     *
     * @param parentSelector селектор родителя
     * @param childSelector  селектор потомка
     * @return найденный элемент
     */
    public SelenideElement findChildElement(String parentSelector, String childSelector) {
        return Selenide.$(parentSelector + " " + childSelector);
    }

    /**
     * Поиск элементов по длине их текста.
     *
     * @param minLength минимальная длина текста
     * @return коллекция найденных элементов
     */
    public ElementsCollection findElementsByTextLength(int minLength) {
        return Selenide.$$x("//p[string-length(text()) > " + minLength + "]");
    }

    /**
     * Поиск элементов по регулярному выражению значения атрибута.
     *
     * @param attributeName имя атрибута
     * @param regex         регулярное выражение
     * @return коллекция найденных элементов
     */
    public ElementsCollection findByAttributeRegex(String attributeName, String regex) {
        return Selenide.$$x("//*[@" + attributeName + " matches '" + regex + "']");
    }

    /**
     * Поиск первого элемента определенного типа.
     *
     * @param elementTag тег элемента
     * @return найденный элемент
     */
    public SelenideElement findFirstOfType(String elementTag) {
        return Selenide.$(elementTag + ":first-of-type");
    }

    /**
     * Поиск элемента с определенной комбинацией классов.
     *
     * @param classes массив имен классов
     * @return найденный элемент
     */
    public SelenideElement findByMultipleClasses(String... classes) {
        String selector = "." + String.join(".", classes);
        return Selenide.$(selector);
    }
}
