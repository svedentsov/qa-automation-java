package com.svedentsov.steps.theinternet;

import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;

/**
 * Класс предоставляет шаги для взаимодействия со страницей "Broken Images".
 */
public class BrokenImagesSteps extends BaseSteps {

    /**
     * Открывает страницу "Broken Images".
     *
     * @return текущий объект BrokenImagesSteps для цепочного вызова методов
     */
    @Step("Открыть страницу 'Broken Images'")
    public BrokenImagesSteps openPage() {
        ui.brokenImagesPage().open();
        return this;
    }

    /**
     * Проверяет, что все изображения на странице видимы.
     *
     * @return текущий объект BrokenImagesSteps для цепочного вызова методов
     */
    @Step("Проверить, что все изображения на странице видимы")
    public BrokenImagesSteps shouldSeeAllImages() {
        ElementsCollection images = ui.brokenImagesPage().IMAGES.getAllElements();
        images.forEach(image -> image.shouldBe(visible));
        return this;
    }

    /**
     * Проверяет, что на странице присутствует определенное количество изображений.
     *
     * @param expectedCount ожидаемое количество изображений
     * @return текущий объект BrokenImagesSteps для цепочного вызова методов
     */
    @Step("Проверить, что на странице присутствует {expectedCount} изображений")
    public BrokenImagesSteps shouldHaveImageCount(int expectedCount) {
        ui.brokenImagesPage().IMAGES.getAllElements().shouldHave(size(expectedCount));
        return this;
    }

    /**
     * Проверяет, что ни одно из изображений на странице не сломано.
     *
     * @return текущий объект BrokenImagesSteps для цепочного вызова методов
     */
    @Step("Проверить, что ни одно из изображений на странице не сломано")
    public BrokenImagesSteps shouldNotHaveBrokenImages() {
        ElementsCollection images = ui.brokenImagesPage().IMAGES.getAllElements();
        images.forEach(image -> image.shouldNotHave(visible));
        return this;
    }
}
