package com.svedentsov.steps.theinternet;

import io.qameta.allure.Step;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс предоставляет шаги для взаимодействия с элементами на странице добавления и удаления.
 */
public class AddRemoveElementsSteps extends BaseSteps {

    /**
     * Проверяет, что текст заголовка соответствует ожидаемому.
     *
     * @param expectedText ожидаемый текст заголовка
     * @return экземпляр {@link AddRemoveElementsSteps} для использования в цепочке вызовов
     */
    @Step("Проверить, что текст заголовка соответствует '{expectedText}'")
    public AddRemoveElementsSteps checkTitleText(String expectedText) {
        ui.addRemoveElementsPage().TITLE_TEXT.checkText(expectedText);
        return this;
    }

    /**
     * Проверяет, что текст заголовка отображается.
     *
     * @return экземпляр {@link AddRemoveElementsSteps} для использования в цепочке вызовов
     */
    @Step("Проверить, что текст заголовка отображается")
    public AddRemoveElementsSteps checkTitleTextIsVisible() {
        ui.addRemoveElementsPage().TITLE_TEXT.shouldBeVisible();
        return this;
    }

    /**
     * Нажимает на кнопку "Add Element" для добавления новой кнопки "Удалить".
     *
     * @return экземпляр {@link AddRemoveElementsSteps} для использования в цепочке вызовов
     */
    @Step("Добавить кнопку 'Удалить'")
    public AddRemoveElementsSteps addDeleteButton() {
        ui.addRemoveElementsPage().ADD_BUTTON.click();
        return this;
    }

    /**
     * Нажимает на кнопку "Delete" для удаления существующей кнопки "Удалить".
     *
     * @return экземпляр {@link AddRemoveElementsSteps} для использования в цепочке вызовов
     */
    @Step("Удалить кнопку 'Удалить'")
    public AddRemoveElementsSteps clickDeleteButton() {
        ui.addRemoveElementsPage().REMOVE_BUTTON.click();
        return this;
    }

    /**
     * Проверяет, что кнопка "Удалить" присутствует на странице.
     *
     * @return экземпляр {@link AddRemoveElementsSteps} для использования в цепочке вызовов
     */
    @Step("Проверить, что кнопка 'Удалить' присутствует")
    public AddRemoveElementsSteps checkDeleteButtonIsExist() {
        ui.addRemoveElementsPage().REMOVE_BUTTON.shouldExist();
        return this;
    }

    /**
     * Проверяет, что кнопка "Удалить" отсутствует на странице.
     *
     * @return экземпляр {@link AddRemoveElementsSteps} для использования в цепочке вызовов
     */
    @Step("Проверить, что кнопка 'Удалить' отсутствует")
    public AddRemoveElementsSteps checkDeleteButtonIsNotExist() {
        ui.addRemoveElementsPage().REMOVE_BUTTON.shouldNotExist();
        return this;
    }

    /**
     * Добавляет указанное количество кнопок "Удалить".
     *
     * @param count количество кнопок для добавления
     * @return экземпляр {@link AddRemoveElementsSteps} для использования в цепочке вызовов
     */
    @Step("Добавить кнопку 'Удалить' {count} раз")
    public AddRemoveElementsSteps addDeleteButtons(int count) {
        IntStream.range(0, count).forEach(i -> addDeleteButton());
        return this;
    }

    /**
     * Удаляет указанное количество кнопок "Удалить".
     *
     * @param count количество кнопок для удаления
     * @return экземпляр {@link AddRemoveElementsSteps} для использования в цепочке вызовов
     */
    @Step("Удалить '{count}' кнопок(и) 'Удалить'")
    public AddRemoveElementsSteps removeDeleteButtons(int count) {
        IntStream.range(0, count).forEach(i -> clickDeleteButton());
        return this;
    }

    /**
     * Проверяет, что количество кнопок "Удалить" равно заданному количеству.
     *
     * @param expectedCount ожидаемое количество кнопок
     * @return экземпляр {@link AddRemoveElementsSteps} для использования в цепочке вызовов
     */
    @Step("Проверить, что количество кнопок 'Удалить' равно '{expectedCount}'")
    public AddRemoveElementsSteps checkNumberOfDeleteButtons(int expectedCount) {
        int actualCount = ui.addRemoveElementsPage().REMOVE_BUTTON.getElementsCount();
        assertEquals(expectedCount, actualCount, "Количество кнопок 'Удалить' не совпадает с ожидаемым");
        return this;
    }
}
