package com.svedentsov.steps.theinternet;

import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;

/**
 * Класс предоставляет шаги для взаимодействия со страницей Challenging DOM.
 */
public class ChallengingDomSteps extends BaseSteps {

    /**
     * Открывает страницу Challenging DOM.
     *
     * @return текущий объект ChallengingDomSteps для цепочного вызова методов
     */
    @Step("Открыть страницу Challenging DOM")
    public ChallengingDomSteps openPage() {
        ui.challengingDomPage().open();
        return this;
    }

    /**
     * Получает идентификатор синей кнопки.
     *
     * @return идентификатор синей кнопки
     */
    @Step("Получить идентификатор синей кнопки")
    public String getBlueButtonId() {
        return ui.challengingDomPage().BLUE_BUTTON.getAttribute("id");
    }

    /**
     * Выполняет клик по синей кнопке.
     *
     * @return текущий объект ChallengingDomSteps для цепочного вызова методов
     */
    @Step("Нажать на синюю кнопку")
    public ChallengingDomSteps clickBlueButton() {
        ui.challengingDomPage().BLUE_BUTTON.click();
        return this;
    }

    /**
     * Нажимает на красную кнопку.
     *
     * @return текущий объект ChallengingDomSteps для цепочного вызова методов
     */
    @Step("Нажать на красную кнопку")
    public ChallengingDomSteps clickRedButton() {
        ui.challengingDomPage().RED_BUTTON.click();
        return this;
    }

    /**
     * Нажимает на зеленую кнопку.
     *
     * @return текущий объект ChallengingDomSteps для цепочного вызова методов
     */
    @Step("Нажать на зеленую кнопку")
    public ChallengingDomSteps clickGreenButton() {
        ui.challengingDomPage().GREEN_BUTTON.click();
        return this;
    }

    /**
     * Проверяет, что текст на холсте отображается корректно.
     *
     * @param expectedText ожидаемый текст на холсте
     * @return текущий объект ChallengingDomSteps для цепочного вызова методов
     */
    @Step("Проверить текст на холсте")
    public ChallengingDomSteps verifyCanvasText(String expectedText) {
        ui.challengingDomPage().CANVAS_WITH_TEXT.checkText(expectedText);
        return this;
    }

    /**
     * Проверяет, что на странице присутствует определенное количество таблиц.
     *
     * @param expectedCount ожидаемое количество таблиц
     * @return текущий объект ChallengingDomSteps для цепочного вызова методов
     */
    @Step("Проверить количество таблиц на странице")
    public ChallengingDomSteps checkTableCount(int expectedCount) {
        ElementsCollection tables = ui.challengingDomPage().TABLE.getAllElements();
        tables.shouldHave(size(expectedCount));
        return this;
    }

    /**
     * Проверяет, что страница загружается полностью.
     *
     * @return текущий объект ChallengingDomSteps для цепочного вызова методов
     */
    @Step("Проверить, что страница загружается полностью")
    public ChallengingDomSteps checkPageIsLoaded() {
        ui.challengingDomPage().isOpened();
        return this;
    }

    /**
     * Проверяет, что элемент холста видим на странице.
     *
     * @return текущий объект ChallengingDomSteps для цепочного вызова методов
     */
    @Step("Проверить, что холст с текстом видим на странице")
    public ChallengingDomSteps shouldSeeCanvasWithText() {
        ui.challengingDomPage().CANVAS_WITH_TEXT.shouldBeVisible();
        return this;
    }
}
