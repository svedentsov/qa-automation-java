package ui.helper;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.utils.DateUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static core.utils.WaitUtils.TIMEOUT;
import static core.utils.WaitUtils.doWait;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Класс {@code Widget} представляет собой базовый класс для работы с элементами веб-страниц используя Selenide.
 *
 * @param <T> тип конкретного виджета, который наследует этот класс
 */
@Getter
@RequiredArgsConstructor
public class Widget<T extends Widget<T>> {

    protected BrowserActions browserActions = new BrowserActions();
    protected UiManager ui = UiManager.getUiManager();
    protected final By locator;

    /**
     * Выполняет клик по видимому и активному элементу.
     *
     * @return текущий виджет
     */
    public T click() {
        $(locator).shouldBe(visible, enabled).click();
        return (T) this;
    }

    /**
     * Выполняет клик по элементу с удержанием клавиши Ctrl.
     */
    public void ctrlClick() {
        createActions()
                .keyDown(Keys.LEFT_CONTROL)
                .click($(locator))
                .keyUp(Keys.LEFT_CONTROL)
                .build().perform();
    }

    /**
     * Выполняет двойной клик по видимому и активному элементу.
     *
     * @return текущий виджет
     */
    public T doubleClick() {
        $(locator).shouldBe(visible, enabled).doubleClick();
        return (T) this;
    }

    /**
     * Выполняет нажатие клавиши Escape.
     */
    public void clickEsc() {
        createActions().sendKeys(Keys.ESCAPE).build().perform();
    }

    /**
     * Выполняет нажатие клавиши Enter на указанном элементе.
     *
     * @param element Элемент, на котором будет выполнено нажатие Enter
     */
    public void clickEnter(SelenideElement element) {
        createActions().sendKeys(element, Keys.ENTER).build().perform();
    }

    /**
     * Выполняет клик правой кнопкой мыши по элементу.
     *
     * @return текущий виджет
     */
    public T contextClick() {
        createActions().contextClick($(locator)).build().perform();
        return (T) this;
    }

    /**
     * Скроллирует страницу к видимому элементу, найденному по локатору.
     *
     * @return текущий виджет
     */
    public T scrollTo() {
        $(locator).should(visible).scrollTo();
        return (T) this;
    }

    /**
     * Проверяет, отображается ли элемент на странице.
     *
     * @return {@code true}, если элемент отображается, иначе {@code false}
     */
    public boolean isDisplayed() {
        return $(locator).isDisplayed();
    }

    /**
     * Проверяет, включен ли элемент.
     *
     * @return {@code true}, если элемент включен, иначе {@code false}
     */
    public boolean isEnabled() {
        return $(locator).isEnabled();
    }

    /**
     * Проверяет, виден ли элемент на странице.
     *
     * @return {@code true}, если элемент виден, иначе {@code false}
     */
    public boolean isVisible() {
        return $(locator).is(visible);
    }

    /**
     * Проверяет, скрыт ли элемент на странице.
     *
     * @return {@code true}, если элемент скрыт, иначе {@code false}
     */
    public boolean isHidden() {
        return $(locator).is(hidden);
    }

    /**
     * Возвращает количество элементов, найденных по локатору.
     *
     * @return количество элементов
     */
    public int getElementCount() {
        return $$(locator).size();
    }

    public ElementsCollection getElement() {
        return $$(locator);
    }

    public ElementsCollection getAllElements() {
        return $$(locator);
    }

    /**
     * Возвращает текст элемента, если он присутствует.
     *
     * @return текст элемента, если он существует, иначе пустая строка
     */
    public String getText() {
        return $(locator).exists() ? $(locator).getText() : "";
    }

    /**
     * Убеждается, что элемент существует на странице.
     *
     * @return текущий виджет
     */
    public T shouldExist() {
        $(locator).should(exist);
        return (T) this;
    }

    /**
     * Убеждается, что элемент не существует на странице.
     *
     * @return текущий виджет
     */
    public T shouldNotExist() {
        $(locator).should(not(exist));
        return (T) this;
    }

    /**
     * Выполняет наведение на видимый и активный элемент.
     *
     * @return текущий виджет
     */
    public T hover() {
        $(locator).shouldBe(visible, enabled).hover();
        return (T) this;
    }

    /**
     * Ожидает, пока элемент не станет видимым.
     *
     * @return текущий виджет
     */
    public T waitToAppear() {
        doWait().untilAsserted(
                () -> assertThat(isDisplayed()).as("Виджет не виден").isTrue());
        return (T) this;
    }

    /**
     * Ожидает исчезновения элемента с использованием стандартного тайм-аута.
     */
    public void waitToDisappear() {
        waitToDisappear(TIMEOUT);
    }

    /**
     * Ожидает, пока элемент не станет видимым в течение указанного времени.
     *
     * @param seconds время ожидания в секундах
     * @return текущий виджет
     */
    public T waitAppear(int seconds) {
        long originalTimeout = Configuration.timeout;
        try {
            Configuration.timeout = 1000L * seconds;
            $(locator).should(appear);
        } finally {
            Configuration.timeout = originalTimeout;
        }
        return (T) this;
    }

    /**
     * Ожидает, пока элемент не исчезнет в течение указанного времени.
     *
     * @param seconds время ожидания в секундах
     * @return текущий виджет
     */
    public T waitDisappear(int seconds) {
        long originalTimeout = Configuration.timeout;
        try {
            Configuration.timeout = 1000L * seconds;
            $(locator).should(disappear);
        } finally {
            Configuration.timeout = originalTimeout;
        }
        return (T) this;
    }

    /**
     * Возвращает список текстовых значений всех найденных элементов по локатору.
     *
     * @return список строк, содержащих текст всех элементов
     */
    public List<String> getAllTexts() {
        return $$(locator).texts();
    }

    /**
     * Вводит текст в элемент.
     *
     * @param text текст для ввода
     * @return текущий виджет
     */
    public T setText(String text) {
        $(locator).shouldBe(visible, enabled).setValue(text);
        return (T) this;
    }

    /**
     * Очищает текстовое поле элемента.
     *
     * @return текущий виджет
     */
    public T clearText() {
        $(locator).shouldBe(visible, enabled).clear();
        return (T) this;
    }

    /**
     * Проверяет, содержит ли элемент указанный текст.
     *
     * @param text текст для проверки
     * @return {@code true}, если элемент содержит текст, иначе {@code false}
     */
    public boolean containsText(String text) {
        return $(locator).shouldBe(visible).getText().contains(text);
    }

    /**
     * Переключает чекбокс.
     *
     * @param check {@code true}, чтобы отметить чекбокс, {@code false} чтобы снять отметку
     * @return текущий виджет
     */
    public T setCheckbox(boolean check) {
        SelenideElement checkbox = $(locator).shouldBe(visible, enabled);
        if (checkbox.isSelected() != check) {
            checkbox.click();
        }
        return (T) this;
    }

    /**
     * Переключает радиокнопку.
     *
     * @return текущий виджет
     */
    public T selectRadioButton() {
        SelenideElement radioButton = $(locator).shouldBe(visible, enabled);
        if (!radioButton.isSelected()) {
            radioButton.click();
        }
        return (T) this;
    }

    /**
     * Выбирает значение из выпадающего списка по видимому тексту.
     *
     * @param text текст для выбора
     * @return текущий виджет
     */
    public T selectOptionByText(String text) {
        $(locator).shouldBe(visible, enabled).selectOption(text);
        return (T) this;
    }

    /**
     * Выбирает значение из выпадающего списка по значению атрибута "value".
     *
     * @param value значение атрибута "value" для выбора
     * @return текущий виджет
     */
    public T selectOptionByValue(String value) {
        $(locator).shouldBe(visible, enabled).selectOptionByValue(value);
        return (T) this;
    }

    /**
     * Получает значение указанного атрибута элемента.
     *
     * @param attributeName имя атрибута
     * @return Значение атрибута
     */
    public String getAttribute(String attributeName) {
        return $(locator).shouldBe(visible).getAttribute(attributeName);
    }

    /**
     * Выполняет перетаскивание элемента на указанный целевой элемент.
     *
     * @param target целевой элемент для перетаскивания
     * @return текущий виджет
     */
    public T dragAndDrop(Widget<?> target) {
        createActions().dragAndDrop($(locator), $(target.getLocator())).build().perform();
        return (T) this;
    }

    /**
     * Ожидает исчезновения элемента в течение указанного времени.
     *
     * @param timeout тайм-аут для ожидания
     */
    protected void waitToDisappear(Duration timeout) {
        doWait().timeout(DateUtil.convert(timeout)).untilAsserted(
                () -> assertThat(isDisplayed()).as("Виджет виден").isFalse());
    }

    /**
     * Создаёт экземпляр {@link Actions} для выполнения действий на странице.
     *
     * @return экземпляр {@link Actions}
     */
    protected Actions createActions() {
        return new Actions(WebDriverRunner.getWebDriver());
    }
}
