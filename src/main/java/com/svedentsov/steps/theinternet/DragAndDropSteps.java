package com.svedentsov.steps.theinternet;

import io.qameta.allure.Step;

/**
 * Класс {@code DragAndDropSteps} предоставляет методы для выполнения действий
 * перетаскивания и проверки текста в контейнерах на странице "Drag and Drop".
 */
public class DragAndDropSteps extends BaseSteps {

    /**
     * Проверяет исходное состояние контейнеров перед выполнением перетаскивания.
     *
     * @param initialTextA ожидаемый текст в контейнере A
     * @param initialTextB ожидаемый текст в контейнере B
     * @return экземпляр {@link DragAndDropSteps} для использования в цепочке вызовов
     */
    @Step("Проверить исходное состояние контейнеров: A = '{initialTextA}', B = '{initialTextB}'")
    public DragAndDropSteps checkInitialState(String initialTextA, String initialTextB) {
        checkContainerAText(initialTextA);
        checkContainerBText(initialTextB);
        return this;
    }

    /**
     * Выполняет перетаскивание контейнера A в контейнер B.
     *
     * @return экземпляр {@link DragAndDropSteps} для использования в цепочке вызовов
     */
    @Step("Перетащить контейнер A в контейнер B")
    public DragAndDropSteps dragAtoB() {
        ui.dragAndDropPage().A_CONTAINER_BUTTON
                .dragAndDrop(ui.dragAndDropPage().B_CONTAINER_BUTTON);
        return this;
    }

    /**
     * Выполняет перетаскивание контейнера B в контейнер A.
     *
     * @return экземпляр {@link DragAndDropSteps} для использования в цепочке вызовов
     */
    @Step("Перетащить контейнер B в контейнер A")
    public DragAndDropSteps dragBtoA() {
        ui.dragAndDropPage().B_CONTAINER_BUTTON
                .dragAndDrop(ui.dragAndDropPage().A_CONTAINER_BUTTON);
        return this;
    }

    /**
     * Проверяет, что контейнер A содержит ожидаемый текст.
     *
     * @param expectedText ожидаемый текст
     * @return экземпляр {@link DragAndDropSteps} для использования в цепочке вызовов
     */
    @Step("Проверить, что контейнер A содержит текст '{expectedText}'")
    public DragAndDropSteps checkContainerAText(String expectedText) {
        ui.dragAndDropPage().A_CONTAINER_BUTTON.checkText(expectedText);
        return this;
    }

    /**
     * Проверяет, что контейнер B содержит ожидаемый текст.
     *
     * @param expectedText ожидаемый текст
     * @return экземпляр {@link DragAndDropSteps} для использования в цепочке вызовов
     */
    @Step("Проверить, что контейнер B содержит текст '{expectedText}'")
    public DragAndDropSteps checkContainerBText(String expectedText) {
        ui.dragAndDropPage().B_CONTAINER_BUTTON.checkText(expectedText);
        return this;
    }

    /**
     * Проверяет, что после перетаскивания контейнера A в контейнер B текст в контейнерах поменялся местами.
     *
     * @param expectedTextA ожидаемый текст в контейнере A после перетаскивания
     * @param expectedTextB ожидаемый текст в контейнере B после перетаскивания
     * @return экземпляр {@link DragAndDropSteps} для использования в цепочке вызовов
     */
    @Step("Проверить, что после перетаскивания контейнера A в контейнер B, текст поменялся местами: A = '{expectedTextA}', B = '{expectedTextB}'")
    public DragAndDropSteps checkTextAfterDragAndDrop(String expectedTextA, String expectedTextB) {
        checkContainerAText(expectedTextA);
        checkContainerBText(expectedTextB);
        return this;
    }
}
