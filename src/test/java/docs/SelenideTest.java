package docs;

import com.codeborne.selenide.*;
import org.openqa.selenium.By;
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
 * селекторами, действиями, проверками, коллекциями, JavaScript, файлами и многого другого.
 */
public class SelenideTest {

    /**
     * Демонстрирует различные команды для управления браузером с использованием Selenide.
     * Включает в себя примеры открытия страниц, работы с окнами браузера,
     * навигационные команды, очистку данных браузера, работу с всплывающими окнами,
     * управление окнами и вкладками, а также переключение между фреймами и окнами.
     */
    void browserCommandExamples() {
        // Открытие страницы
        Selenide.open("https://google.com"); // Абсолютный путь
        Selenide.open("/customer/orders"); // Относительный путь

        // Открытие страницы с базовой аутентификацией
        Selenide.open("/", AuthenticationType.BASIC, new BasicAuthCredentials("user", "password"));

        // Навигационные команды
        Selenide.back(); // Кнопка назад
        Selenide.refresh(); // Обновить страницу
        Selenide.forward(); // Кнопка вперед

        // Очистка данных
        Selenide.clearBrowserCookies(); // Очистка файлов куки
        Selenide.clearBrowserLocalStorage(); // Очистка Local Storage

        // Всплывающие окна
        Selenide.confirm(); // Подтверждение во всплывающих окнах
        Selenide.dismiss(); // Отмена во всплывающих окнах
        Selenide.prompt("Введите значение"); // Ввод значения в prompt

        // Управление окнами и вкладками
        Selenide.switchTo().window(1); // Переключение на вторую вкладку
        Selenide.closeWindow(); // Закрыть активную вкладку
        Selenide.closeWebDriver(); // Закрыть все вкладки и браузер

        // Переключение между фреймами
        Selenide.switchTo().frame("new"); // Переключение на фрейм по имени
        Selenide.switchTo().frame(0); // Переключение на первый фрейм по индексу
        Selenide.switchTo().defaultContent(); // Переключение на основной контент
        Selenide.switchTo().window("The Internet"); // Переключение на окно по названию
    }

    /**
     * Демонстрирует различные способы использования селекторов в Selenide для поиска элементов на странице.
     * Включает примеры поиска элементов по тегу, индексу, XPath, тексту, структуре DOM,
     * атрибутам, ID и классам. Показаны различные методы и варианты записи селекторов.
     */
    void selectorsExamples() {
        // Поиск элементов
        Selenide.$("div").click(); // Поиск первого div и клик по нему
        Selenide.$("div", 2).click(); // Поиск третьего div и клик по нему
        Selenide.$x("//h1/div").click(); // Поиск по XPath
        Selenide.$(byXpath("//h1/div")).click(); // Поиск по XPath (альтернатива)
        Selenide.$(By.xpath("//h1/div")).click(); // Поиск по XPath с использованием WebDriver By

        // Поиск по тексту
        Selenide.$(byText("полный текст")).click(); // Поиск по полной строке
        Selenide.$(withText("часть текста")).click(); // Поиск по подстроке

        // Поиск по CSS селекторам
        Selenide.$(".className").click(); // Поиск по классу
        Selenide.$("#elementId").click(); // Поиск по ID
        Selenide.$("[name='elementName']").click(); // Поиск по атрибуту name

        // Поиск по структуре DOM
        Selenide.$("div").parent(); // Поиск родительского элемента
        Selenide.$("div").sibling(2); // Поиск третьего соседнего элемента
        Selenide.$("div").preceding(0); // Поиск первого предшествующего элемента
        Selenide.$("div").closest("ul"); // Поиск ближайшего предка с тегом ul
        Selenide.$("div:last-child"); // Поиск последнего дочернего элемента
        Selenide.$("div").$("h1").find(byText("abc")).click(); // Поиск элемента внутри другого элемента

        // Поиск по атрибуту
        Selenide.$(byAttribute("data-test", "value")).click(); // Поиск по атрибуту
        Selenide.$("[data-test='value']").click(); // Поиск по атрибуту (альтернатива)

        // Поиск по CSS псевдоклассам
        Selenide.$("input:enabled"); // Поиск включенных input
        Selenide.$("input:disabled"); // Поиск отключенных input
        Selenide.$("input:checked"); // Поиск отмеченных checkbox или radio

        // Комбинация селекторов
        Selenide.$("div.classname#idname[data-test='value']"); // Комбинированный селектор
    }

    /**
     * Демонстрирует различные действия, которые можно выполнить с помощью Selenide.
     * Содержит примеры работы с мышью (клик, двойной клик, клик правой кнопкой, наведение),
     * работы с текстовыми полями (установка значения, добавление текста, очистка),
     * взаимодействия с клавиатурой (отправка клавиш, нажатие специальных клавиш),
     * выполнения сложных действий с использованием Actions,
     * и примеры выбора опций в выпадающих списках и радио-кнопках.
     */
    void actionsExamples() {
        // Работа с мышью
        Selenide.$("button").click(); // Клик по элементу
        Selenide.$("button").doubleClick(); // Двойной клик
        Selenide.$("button").contextClick(); // Клик ПКМ
        Selenide.$("div").hover(); // Подвести курсор

        // Работа с текстовыми полями
        Selenide.$("input").setValue("text"); // Очистить поле и установить значение
        Selenide.$("input").append("more text"); // Добавить текст без очистки поля
        Selenide.$("input").clear(); // Очистить поле
        Selenide.$("input").setValue(""); // Очистить поле путем установки пустого значения

        // Работа с клавишами
        Selenide.$("input").sendKeys("Hello"); // Ввод текста
        Selenide.actions().sendKeys(Keys.ENTER).perform(); // Нажать Enter на уровне приложения
        Selenide.$("input").pressEnter(); // Нажать Enter на конкретном элементе
        Selenide.$("input").pressEscape(); // Нажать Esc
        Selenide.$("input").pressTab(); // Нажать Tab

        // Сложные действия
        Selenide.actions()
                .moveToElement(Selenide.$("div"))
                .clickAndHold()
                .moveByOffset(100, 100)
                .release()
                .perform(); // Пример drag-and-drop

        // Выбор опций
        Selenide.$("select").selectOption("Option 1"); // Выбор опции по видимому тексту
        Selenide.$("select").selectOptionByValue("option1"); // Выбор опции по значению value
        Selenide.$("select").selectOption(2); // Выбор опции по индексу

        // Работа с чекбоксами и радио-кнопками
        Selenide.$("input[type='checkbox']").setSelected(true); // Отметить чекбокс
        Selenide.$("input[type='radio'][value='radio1']").click(); // Выбрать радио-кнопку
    }

    /**
     * Демонстрирует различные проверки (ассерты) элементов на странице с использованием Selenide.
     * Включает проверки видимости элемента, наличия или отсутствия текста,
     * проверку появления и исчезновения элементов,
     * а также использование пользовательских таймаутов для ожидания определенного состояния элемента.
     */
    void assertionsExamples() {
        Selenide.$("div").shouldBe(visible); // Проверка видимости элемента
        Selenide.$("div").shouldNotBe(visible); // Проверка, что элемент не видим
        Selenide.$("div").shouldHave(text("Welcome")); // Проверка наличия текста
        Selenide.$("div").shouldNotHave(text("Error")); // Проверка отсутствия текста
        Selenide.$("div").should(appear); // Проверка появления элемента
        Selenide.$("div").should(disappear); // Проверка исчезновения элемента
        Selenide.$("div").shouldBe(enabled); // Проверка, что элемент активен
        Selenide.$("input").shouldHave(value("Test")); // Проверка значения поля ввода
        Selenide.$("input").shouldBe(empty); // Проверка, что поле ввода пустое
        Selenide.$("input").shouldHave(attribute("placeholder", "Enter name")); // Проверка атрибута
        Selenide.$("img").shouldHave(attributeMatching("src", ".*\\.png")); // Проверка соответствия атрибута регулярному выражению

        // Проверка с пользовательским таймаутом
        Selenide.$("div").shouldBe(visible, Duration.ofSeconds(30)); // Проверка видимости с кастомным таймаутом
    }

    /**
     * Демонстрирует различные условия (Conditions) и методы проверки состояния элементов и коллекций в Selenide.
     * Включает примеры фильтрации коллекций по условию, исключения элементов из коллекции,
     * навигации по элементам коллекции, проверки размеров коллекций,
     * проверки текста в элементах коллекций, комбинированных проверок,
     * а также дополнительных методов работы с коллекциями.
     */
    void conditionsExamples() {
        // Фильтрация коллекций
        ElementsCollection items = Selenide.$$("div.item");
        items.filterBy(text("Active")).shouldHave(size(2)); // Фильтрация элементов по тексту
        items.filterBy(cssClass("active")).shouldHave(sizeGreaterThan(1)); // Фильтрация по CSS классу
        items.filterBy(attribute("data-type", "primary")).shouldHave(size(3)); // Фильтрация по атрибуту

        // Исключение элементов из коллекции
        items.excludeWith(text("Inactive")).shouldHave(size(1)); // Исключение элементов с указанным текстом
        items.excludeWith(cssClass("disabled")).shouldBe(sizeGreaterThan(2)); // Исключение по CSS классу

        // Навигация по коллекциям
        items.first().click(); // Клик по первому элементу
        items.last().click(); // Клик по последнему элементу
        items.get(1).click(); // Клик по элементу с индексом 1 (второй элемент)
        Selenide.$("div.item", 1).click(); // Альтернативный способ клика по элементу с индексом 1
        items.findBy(text("Settings")).click(); // Клик по элементу с текстом "Settings"
        items.filterBy(visible).get(2).click(); // Клик по третьему видимому элементу

        // Проверки размеров коллекций
        items.shouldHave(size(5)); // Проверка точного размера коллекции
        items.shouldHave(sizeGreaterThan(4)); // Проверка, что размер коллекции больше 4
        items.shouldHave(sizeLessThan(10)); // Проверка, что размер коллекции меньше 10

        // Проверки текста в коллекциях
        items.shouldHave(texts("Home", "Profile", "Settings")); // Проверка наличия текстов в элементах коллекции
        items.shouldHave(exactTexts("Home", "Profile", "Settings")); // Проверка полного совпадения текста
        items.shouldHave(textsInAnyOrder("Settings", "Profile", "Home")); // Проверка текста без учета порядка
        items.shouldHave(exactTextsCaseSensitiveInAnyOrder("Profile", "Settings", "Home")); // Проверка текста с учетом регистра и порядка

        // Проверка наличия конкретного элемента
        items.shouldHave(itemWithText("Logout")); // Проверка наличия элемента с текстом "Logout"
        items.findBy(exactText("Logout")).should(exist); // Проверка существования элемента с точным текстом "Logout"

        // Комбинированные проверки
        items.filterBy(visible).shouldHave(sizeGreaterThan(0)).get(0).shouldHave(text("Dashboard")); // Комбинированная проверка видимости и текста

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
     * Демонстрирует различные операции с коллекциями элементов в Selenide.
     * Включает примеры фильтрации коллекций, навигации по элементам коллекции,
     * проверки размеров и содержимого коллекций, поиска элементов по тексту,
     * а также проверки порядка элементов в коллекции.
     */
    void collectionsExamples() {
        // Фильтрация коллекций
        ElementsCollection divs = Selenide.$$("div");
        divs.filterBy(text("123")).shouldHave(size(1)); // Фильтрация по тексту
        divs.excludeWith(text("123")).shouldHave(sizeGreaterThan(0)); // Исключение по тексту

        // Навигация по коллекциям
        divs.first().click(); // Первый элемент
        divs.last().click(); // Последний элемент
        divs.get(1).click(); // Элемент по индексу (второй)
        Selenide.$("div", 1).click(); // Аналогично предыдущему
        divs.findBy(text("123")).click(); // Найти первый элемент с текстом

        // Проверки коллекций
        divs.shouldHave(size(3)); // Проверка размера коллекции
        divs.shouldBe(CollectionCondition.empty); // Проверка, что коллекция пуста

        // Проверка текста в коллекциях
        divs.shouldHave(texts("Alfa", "Beta", "Gamma")); // Проверка наличия текста
        divs.shouldHave(exactTexts("Alfa", "Beta", "Gamma")); // Проверка полного совпадения текста

        // Проверка порядка текста
        divs.shouldHave(textsInAnyOrder("Beta", "Gamma", "Alfa")); // Проверка текста без учета порядка
        divs.shouldHave(exactTextsCaseSensitiveInAnyOrder("Beta", "Gamma", "Alfa")); // Проверка точного совпадения текста без учета порядка

        // Поиск конкретного элемента
        divs.shouldHave(itemWithText("Gamma")); // Поиск элемента с конкретным текстом

        // Проверка размера коллекции
        divs.shouldHave(sizeGreaterThan(0)); // Размер больше нуля
        divs.shouldHave(sizeGreaterThanOrEqual(1)); // Размер больше или равен одному
        divs.shouldHave(sizeLessThan(3)); // Размер меньше трех
        divs.shouldHave(sizeLessThanOrEqual(2)); // Размер меньше или равен двум
    }

    /**
     * Демонстрирует выполнение JavaScript-кода с использованием Selenide.
     * Включает примеры выполнения синхронного и асинхронного JavaScript,
     * передачи аргументов в JavaScript-функции, а также получения результатов выполнения скриптов.
     */
    void jsExamples() {
        // Выполнение JavaScript
        Selenide.executeJavaScript("alert('Hello, Selenide!')");
        Selenide.executeJavaScript("arguments[0].click();", Selenide.$("button"));
        Selenide.executeAsyncJavaScript("window.setTimeout(arguments[arguments.length - 1], 5000);"); // Асинхронный скрипт с задержкой
        long value = Selenide.executeJavaScript("return document.getElementsByTagName('div').length;");
        System.out.println("Количество div на странице: " + value);
    }

    /**
     * Демонстрирует работу с файлами в Selenide, включая загрузку и загрузку файлов.
     * Включает примеры скачивания файлов, загрузки файлов из classpath и локальной файловой системы,
     * а также методы для работы с полями ввода файлов.
     *
     * @throws FileNotFoundException если файл не найден при загрузке
     */
    void filesExamples() throws FileNotFoundException {
        // Загрузка файлов
        File file1 = Selenide.$("a.fileLink").download(); // Загрузка файла через ссылку
        File file2 = Selenide.$("div").download(DownloadOptions.using(FileDownloadMode.PROXY)); // Загрузка файла с настройками

        // Проверка содержимого файлов
        Selenide.$("input[type='file']").uploadFromClasspath("files/hello_world.txt"); // Загрузка файла из classpath
        Selenide.$("input[type='file']").uploadFile(new File("src/test/resources/hello_world.txt")); // Загрузка локального файла

        // Множественная загрузка файлов
        Selenide.$("input[type='file']").uploadFromClasspath("files/hello_world.txt", "files/readme.txt"); // Загрузка нескольких файлов

        // Очистка поля загрузки файлов
        Selenide.$("input[type='file']").setValue(""); // Очистка поля загрузки файлов
    }

    /**
     * Демонстрирует работу с диалоговыми окнами и всплывающими сообщениями в Selenide.
     * Включает примеры работы с alert, confirm, prompt.
     */
    void dialogsExamples() {
        // Работа с alert
        Selenide.$("button.alert").click();
        Selenide.switchTo().alert().accept(); // Подтвердить alert
        Selenide.switchTo().alert().dismiss(); // Отклонить alert
        String alertText = Selenide.switchTo().alert().getText(); // Получить текст alert
        Selenide.switchTo().alert().sendKeys("Some text"); // Ввести текст в prompt

        // Проверка текста в alert
        Selenide.$("button.alert").click();
    }

    /**
     * Демонстрирует работу с настройками Selenide.
     * Включает примеры установки конфигурационных параметров, таких как таймауты, браузер,
     * режимы загрузки файлов, а также использование конфигурации внутри тестов.
     */
    void configurationExamples() {
        // Установка конфигурации
        Configuration.timeout = 10000; // Установка таймаута в миллисекундах
        Configuration.browser = "chrome"; // Выбор браузера
        Configuration.headless = true; // Запуск в режиме без интерфейса

        // Использование конфигурации в тесте
        Selenide.open("https://example.com");
    }

    /**
     * Демонстрирует работу с событиями мыши и клавиатуры, такими как drag-and-drop, scroll, и другими.
     * Включает примеры выполнения операций перетаскивания, прокрутки к элементу,
     * фокусировки на элементе, а также работы с горячими клавишами.
     */
    void mouseAndKeyboardExamples() {
        // Прокрутка к элементу
        Selenide.$("div").scrollTo(); // Прокрутка страницы к элементу

        // Фокус на элементе
        Selenide.$("input").click(); // Клик по полю ввода для установки фокуса

        // Работа с горячими клавишами
        Selenide.actions().keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform(); // Выделение всего текста (Ctrl + A)
    }

    /**
     * Демонстрирует работу с HTML5 элементами, такими как видео, аудио, localStorage и sessionStorage.
     * Включает примеры взаимодействия с localStorage, проверки состояния видео и аудио элементов.
     */
    void html5Examples() {
        // Работа с localStorage
        Selenide.executeJavaScript("localStorage.setItem('token', '12345');");
        String token = Selenide.executeJavaScript("return localStorage.getItem('token');");
        System.out.println("Token из localStorage: " + token);

        // Проверка состояния видео
        Selenide.$("video").shouldHave(attribute("paused", "false")); // Проверка, что видео играет
    }

    /**
     * Демонстрирует создание скриншотов и сохранение источника страницы.
     * Включает примеры создания скриншота всей страницы, отдельного элемента, а также сохранение HTML-кода страницы.
     */
    void screenshotsExamples() {
        // Скриншот всей страницы
        Selenide.screenshot("fullPage");

        // Скриншот элемента
        Selenide.$("div").screenshot();

        // Сохранение HTML-кода страницы
        String pageSource = WebDriverRunner.getWebDriver().getPageSource();
    }
}
