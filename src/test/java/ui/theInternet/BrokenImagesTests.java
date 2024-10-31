package ui.theInternet;

import common.UITest;
import core.annotations.Layer;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Layer("ui")
@Feature("Тестирование страницы с изображениями")
@DisplayName("Тесты страницы 'Broken Images'")
public class BrokenImagesTests extends UITest {

    @Test
    @DisplayName("Проверка видимости всех изображений на странице")
    @Description("Проверяет, что все изображения на странице 'Broken Images' видимы.")
    public void testAllImagesVisible() {
        theInternet.brokenImagesSteps()
                .openPage()
                .shouldSeeAllImages();
    }

    @Test
    @DisplayName("Проверка количества изображений на странице")
    @Description("Проверяет, что на странице 'Broken Images' присутствует определенное количество изображений.")
    public void testImageCount() {
        theInternet.brokenImagesSteps()
                .openPage()
                .shouldHaveImageCount(3);
    }

    @Test
    @DisplayName("Проверка отсутствия сломанных изображений на странице")
    @Description("Проверяет, что на странице 'Broken Images' нет сломанных изображений.")
    public void testNoBrokenImages() {
        theInternet.brokenImagesSteps()
                .openPage()
                .shouldNotHaveBrokenImages();
    }
}
