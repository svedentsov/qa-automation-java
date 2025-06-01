package com.svedentsov.steps.theinternet;

import io.qameta.allure.Step;

/**
 * Класс предоставляет шаги для взаимодействия со страницей A/B Testing.
 */
public class AbTestSteps extends BaseSteps {

    /**
     * Проверяет, что заголовок страницы отображается.
     *
     * @return текущий объект AbTestSteps для цепочки вызовов
     */
    @Step("Проверить, что заголовок страницы отображается")
    public AbTestSteps checkTitleIsVisible() {
        ui.abTestPage().TITLE_TEXT.shouldBeVisible();
        return this;
    }

    /**
     * Проверяет, что текст заголовка страницы соответствует ожидаемому значению.
     *
     * @param expectedTitle ожидаемый текст заголовка страницы
     * @return текущий объект AbTestSteps для цепочки вызовов
     */
    @Step("Проверить, что текст заголовка страницы соответствует ожидаемому значению: {expectedTitle}")
    public AbTestSteps checkTitleText(String expectedTitle) {
        ui.abTestPage().TITLE_TEXT.checkText(expectedTitle);
        return this;
    }

    /**
     * Проверяет, что описание эксперимента отображается.
     *
     * @return текущий объект AbTestSteps для цепочки вызовов
     */
    @Step("Проверить, что описание эксперимента отображается")
    public AbTestSteps checkDescriptionIsVisible() {
        ui.abTestPage().DESCRIPTION_TEXT.shouldBeVisible();
        return this;
    }

    /**
     * Проверяет, что текст описания эксперимента соответствует ожидаемому значению.
     *
     * @param expectedDescription ожидаемый текст описания эксперимента
     * @return текущий объект AbTestSteps для цепочки вызовов
     */
    @Step("Проверить, что текст описания эксперимента соответствует ожидаемому значению: {expectedDescription}")
    public AbTestSteps checkDescriptionText(String expectedDescription) {
        ui.abTestPage().DESCRIPTION_TEXT.checkText(expectedDescription);
        return this;
    }
}
