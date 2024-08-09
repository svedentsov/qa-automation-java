package docs.snippets;

import com.codeborne.selenide.*;
import org.openqa.selenium.Keys;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;

/**
 * Класс содержит примеры использования различных функций Selenide, таких как команды для работы с браузером,
 * селекторами, действиями, проверками, коллекциями, JavaScript и файлами.
 */
public class SelenideSnippets {

    /**
     * Примеры команд для управления браузером.
     */
    void browserCommandExamples() {
        // Открытие страницы
        Selenide.open("https://google.com"); // Абсолютный путь
        Selenide.open("/customer/orders"); // Относительный путь

        // Работа с браузерными окнами
        Selenide.open("/", String.valueOf(AuthenticationType.BASIC), "user", "password"); // Открытие с авторизацией

        // Навигационные команды
        Selenide.back(); // Кнопка назад
        Selenide.refresh(); // Обновить страницу

        // Очистка данных
        Selenide.clearBrowserCookies(); // Очистка файлов куки
        Selenide.clearBrowserLocalStorage(); // Очистка Local Storage

        // Всплывающие окна
        Selenide.confirm(); // Подтверждение во всплывающих окнах
        Selenide.dismiss(); // Отмена во всплывающих окнах

        // Управление окнами и вкладками
        Selenide.closeWindow(); // Закрыть активную вкладку
        Selenide.closeWebDriver(); // Закрыть все вкладки и браузер

        // Переключение между фреймами
        Selenide.switchTo().frame("new"); // Переключение на фрейм
        Selenide.switchTo().defaultContent(); // Переключение на основной контент
        Selenide.switchTo().window("The Internet"); // Переключение на окно по названию
    }

    /**
     * Примеры использования различных селекторов для поиска элементов на странице.
     */
    void selectorsExamples() {
        // Поиск элементов
        Selenide.$("div").click(); // Поиск первого div и клик по нему
        Selenide.$("div", 2).click(); // Поиск третьего div и клик по нему
        Selenide.$x("//h1/div").click(); // Поиск по XPath
        Selenide.$(byXpath("//h1/div")).click(); // Поиск по XPath (альтернатива)

        // Поиск по тексту
        Selenide.$(byText("full text")).click(); // Поиск по полной строке
        Selenide.$(withText("ull tex")).click(); // Поиск по подстроке

        // Поиск по структуре DOM
        Selenide.$("").parent(); // Поиск родительского элемента
        Selenide.$("").sibling(2); // Поиск третьего соседнего элемента
        Selenide.$("").preceding(0); // Поиск первого предшествующего элемента
        Selenide.$("").closest("div"); // Поиск ближайшего предка с тегом div
        Selenide.$("div:last-child"); // Поиск последнего дочернего элемента
        Selenide.$("div").$("h1").find(byText("abc")).click(); // Поиск элемента внутри другого элемента

        // Поиск по атрибуту
        Selenide.$(byAttribute("abc", "x")).click(); // Поиск по атрибуту
        Selenide.$("[abc=x]").click(); // Поиск по атрибуту (альтернатива)

        // Поиск по ID
        Selenide.$(byId("mytext")).click(); // Поиск по ID (Selenide)
        Selenide.$("#mytext").click(); // Поиск по ID (CSS)

        // Поиск по классу
        Selenide.$(byClassName("red")).click(); // Поиск по классу (Selenide)
        Selenide.$(".red").click(); // Поиск по классу (CSS)
    }

    /**
     * Примеры различных действий, таких как клики, работа с текстовыми полями и клавишами.
     */
    void actionsExamples() {
        // Работа с мышью
        Selenide.$("").click(); // Клик по элементу
        Selenide.$("").doubleClick(); // Двойной клик
        Selenide.$("").contextClick(); // Клик ПКМ
        Selenide.$("").hover(); // Подвести курсор

        // Работа с текстовыми полями
        Selenide.$("").setValue("text"); // Очистить поле и установить значение
        Selenide.$("").append("text"); // Добавить текст без очистки поля
        Selenide.$("").clear(); // Очистить поле
        Selenide.$("").setValue(""); // Очистить поле путем установки пустого значения

        // Работа с клавишами
        Selenide.$("div").sendKeys("c"); // Нажать клавишу на конкретном элементе
        Selenide.actions().sendKeys("c").perform(); // Нажать клавишу на всем приложении
        Selenide.actions().sendKeys(Keys.chord(Keys.CONTROL, "f")).perform(); // Нажать Ctrl + F
        Selenide.$("html").sendKeys(Keys.chord(Keys.CONTROL, "f")); // Нажать Ctrl + F на всю страницу
        Selenide.$("").pressEnter(); // Нажать Enter
        Selenide.$("").pressEscape(); // Нажать Esc
        Selenide.$("").pressTab(); // Нажать Tab

        // Сложные действия
        Selenide.actions().moveToElement(Selenide.$("div")).clickAndHold().moveByOffset(300, 200).release().perform(); // Пример сложных действий

        // Выбор опций
        Selenide.$("").selectOption("dropdown_option"); // Выбор опции в выпадающем списке
        Selenide.$("").selectRadio("radio_options"); // Выбор радио-кнопки
    }

    /**
     * Примеры проверок элементов, таких как видимость, наличие текста, атрибуты и состояния элементов.
     */
    void assertionsExamples() {
        Selenide.$("").shouldBe(visible); // Проверка видимости элемента
        Selenide.$("").shouldNotBe(visible); // Проверка, что элемент не видим
        Selenide.$("").shouldHave(text("abc")); // Проверка наличия текста
        Selenide.$("").shouldNotHave(text("abc")); // Проверка отсутствия текста
        Selenide.$("").should(appear); // Проверка появления элемента
        Selenide.$("").shouldNot(appear); // Проверка отсутствия элемента
        Selenide.$("").shouldBe(visible, Duration.ofSeconds(30)); // Проверка видимости с кастомным таймаутом
    }

    /**
     * Примеры использования условий для проверки состояния элементов.
     */
    void conditionsExamples() {
        // Фильтрация коллекций
        Selenide.$$("div").filterBy(text("123")).shouldHave(size(1)); // Фильтрация элементов по тексту
        Selenide.$$("div").filterBy(cssClass("active")).shouldHave(sizeGreaterThan(1)); // Фильтрация по CSS классу
        Selenide.$$("input").filterBy(attribute("type", "checkbox")).shouldHave(size(3)); // Фильтрация по атрибуту

        // Исключение элементов из коллекции
        Selenide.$$("div").excludeWith(text("123")).shouldHave(size(1)); // Исключение элементов с указанным текстом
        Selenide.$$("div").excludeWith(cssClass("inactive")).shouldBe(sizeGreaterThan(2)); // Исключение по CSS классу

        // Навигация по коллекциям
        Selenide.$$("div").first().click(); // Клик по первому элементу
        Selenide.$$("div").last().click(); // Клик по последнему элементу
        Selenide.$$("div").get(1).click(); // Клик по элементу с индексом 1 (второй элемент)
        Selenide.$("div", 1).click(); // Альтернативный способ клика по элементу с индексом 1
        Selenide.$$("div").findBy(text("123")).click(); // Клик по первому элементу с текстом "123"
        Selenide.$$("div").filterBy(visible).get(2).click(); // Клик по третьему видимому элементу

        // Проверки размеров коллекций
        Selenide.$$("div").shouldHave(size(3)); // Проверка точного размера коллекции
        Selenide.$$("div").shouldHave(sizeGreaterThan(2)); // Проверка, что размер коллекции больше 2
        Selenide.$$("div").shouldHave(sizeLessThan(5)); // Проверка, что размер коллекции меньше 5
        Selenide.$$("div").shouldHave(sizeLessThanOrEqual(4)); // Проверка, что размер коллекции меньше или равен 4
        Selenide.$$("div").shouldHave(sizeGreaterThanOrEqual(2)); // Проверка, что размер коллекции больше или равен 2

        // Проверки текста в коллекциях
        Selenide.$$("div").shouldHave(texts("Alfa", "Beta", "Gamma")); // Проверка наличия текстов в элементах коллекции
        Selenide.$$("div").shouldHave(exactTexts("Alfa", "Beta", "Gamma")); // Проверка полного совпадения текста
        Selenide.$$("div").shouldHave(textsInAnyOrder("Gamma", "Beta", "Alfa")); // Проверка текста без учета порядка
        Selenide.$$("div").shouldHave(exactTextsCaseSensitiveInAnyOrder("Beta", "Gamma", "Alfa")); // Проверка текста с учетом регистра и порядка

        // Проверка наличия конкретного элемента
        Selenide.$$("div").shouldHave(itemWithText("Gamma")); // Проверка наличия элемента с текстом "Gamma"
        Selenide.$$("div").findBy(exactText("Gamma")).should(exist); // Проверка существования элемента с точным текстом "Gamma"

        // Комбинированные проверки
        Selenide.$$("div").filterBy(visible).shouldHave(sizeGreaterThan(0)).get(0).shouldHave(text("Visible Text")); // Комбинированная проверка видимости и текста

        // Дополнительные методы работы с коллекциями
        Selenide.$$("ul li").filter(visible).shouldHave(size(5)); // Фильтрация видимых элементов списка и проверка их количества
        Selenide.$$("div").snapshot().shouldHave(size(3)); // Создание моментального снимка коллекции и проверка размера
        Selenide.$$("div").texts(); // Получение списка всех текстов в коллекции
        Selenide.$$("div").asFixedIterable().forEach(element -> element.shouldBe(visible)); // Проход по каждому элементу коллекции с проверкой видимости

        // Примеры сложных действий с коллекцией
        Selenide.$$("table tr").shouldHave(sizeGreaterThan(5)); // Проверка количества строк в таблице
        Selenide.$$("table tr").filterBy(cssClass("selected")).shouldHave(size(3)); // Проверка количества выбранных строк
        Selenide.$$("table tr").filterBy(cssClass("selected")).excludeWith(text("Inactive")).shouldHave(size(2)); // Исключение элементов с определенным текстом

        // Псевдо-элементы в коллекциях
        Selenide.$$("div:nth-child(2)").shouldHave(size(1)); // Проверка размера коллекции с использованием псевдо-классов
        Selenide.$$("div:empty").shouldHave(size(2)); // Проверка размера коллекции для пустых элементов
    }

    /**
     * Примеры работы с коллекциями элементов.
     */
    void collectionsExamples() {
        // Фильтрация коллекций
        Selenide.$$("div").filterBy(text("123")).shouldHave(size(1)); // Фильтрация по тексту
        Selenide.$$("div").excludeWith(text("123")).shouldHave(size(1)); // Исключение по тексту

        // Навигация по коллекциям
        Selenide.$$("div").first().click(); // Первый элемент
        Selenide.$$("div").last().click(); // Последний элемент
        Selenide.$$("div").get(1).click(); // Элемент по индексу (второй)
        Selenide.$("div", 1).click(); // Аналогично предыдущему
        Selenide.$$("div").findBy(text("123")).click(); // Найти первый элемент с текстом

        // Проверки коллекций
        Selenide.$$("").shouldHave(size(0)); // Проверка размера коллекции
        Selenide.$$("").shouldBe(CollectionCondition.empty); // Проверка, что коллекция пуста

        // Проверка текста в коллекциях
        Selenide.$$("").shouldHave(texts("Alfa", "Beta", "Gamma")); // Проверка наличия текста
        Selenide.$$("").shouldHave(exactTexts("Alfa", "Beta", "Gamma")); // Проверка полного совпадения текста

        // Проверка порядка текста
        Selenide.$$("").shouldHave(textsInAnyOrder("Beta", "Gamma", "Alfa")); // Проверка текста без учета порядка
        Selenide.$$("").shouldHave(exactTextsCaseSensitiveInAnyOrder("Beta", "Gamma", "Alfa")); // Проверка точного совпадения текста без учета порядка

        // Поиск конкретного элемента
        Selenide.$$("").shouldHave(itemWithText("Gamma")); // Поиск элемента с конкретным текстом

        // Проверка размера коллекции
        Selenide.$$("").shouldHave(sizeGreaterThan(0)); // Размер больше нуля
        Selenide.$$("").shouldHave(sizeGreaterThanOrEqual(1)); // Размер больше или равен одному
        Selenide.$$("").shouldHave(sizeLessThan(3)); // Размер меньше трех
        Selenide.$$("").shouldHave(sizeLessThanOrEqual(2)); // Размер меньше или равен двум
    }

    /**
     * Примеры выполнения JavaScript-кода.
     */
    void jsExamples() {
        // Выполнение JavaScript
        Selenide.executeJavaScript("alert('selenide')");
        Selenide.executeJavaScript("alert('selenide')", Selenide.$("div"));
        Selenide.executeAsyncJavaScript("alert('selenide')");
        long value = Selenide.executeJavaScript("return 10;");
    }

    /**
     * Примеры работы с файлами, такими как загрузка и проверка содержимого.
     */
    void filesExamples() throws FileNotFoundException {
        // Загрузка файлов
        File file1 = Selenide.$("a.fileLink").download(); // Загрузка файла через ссылку
        File file2 = Selenide.$("div").download(DownloadOptions.using(FileDownloadMode.PROXY)); // Загрузка файла с настройками

        // Проверка содержимого файлов
        Selenide.$("uploadLink").uploadFromClasspath("files/hello_world.txt"); // Загрузка файла из classpath
        Selenide.$("uploadLink").uploadFile(new File("src/test/resources/hello_world.txt")); // Загрузка локального файла

        // Очистка загрузок
        Selenide.$("").sendKeys(Keys.chord(Keys.CONTROL, "a"), "hello"); // Отправка текста с очисткой поля
        Selenide.$("").setValue("hello"); // Установка значения в поле
        Selenide.$("").clear(); // Очистка поля ввода
    }
}
