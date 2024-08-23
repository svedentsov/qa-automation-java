package ui.theInternet;

import common.UITest;
import core.annotations.Layer;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Layer("ui")
@Feature("Тестирование функционала страницы Challenging DOM")
@DisplayName("Тесты страницы 'Challenging DOM'")
public class ChallengingDomTests extends UITest {

    @Test
    @DisplayName("Проверка видимости холста на странице")
    @Description("Проверяет, что элемент холста с текстом отображается на странице 'Challenging DOM'.")
    public void testCanvasIsVisible() {
        theInternet.challengingDomSteps()
                .openPage()
                .shouldSeeCanvasWithText();
    }

    @Test
    @DisplayName("Нажатие на синюю кнопку")
    @Description("Проверяет, что после нажатия на синюю кнопку происходит корректное взаимодействие с элементом.")
    public void testClickBlueButton() {
        theInternet.challengingDomSteps()
                .openPage()
                .clickBlueButton()
                .shouldSeeCanvasWithText();
    }

    @Test
    @DisplayName("Нажатие на красную кнопку")
    @Description("Проверяет, что после нажатия на красную кнопку происходит корректное взаимодействие с элементом.")
    public void testClickRedButton() {
        theInternet.challengingDomSteps()
                .openPage()
                .clickRedButton()
                .shouldSeeCanvasWithText();
    }

    @Test
    @DisplayName("Нажатие на зеленую кнопку")
    @Description("Проверяет, что после нажатия на зеленую кнопку происходит корректное взаимодействие с элементом.")
    public void testClickGreenButton() {
        theInternet.challengingDomSteps()
                .openPage()
                .clickGreenButton()
                .shouldSeeCanvasWithText();
    }

    @Test
    @DisplayName("Проверка текста на холсте")
    @Description("Проверяет, что текст на холсте отображается правильно после загрузки страницы.")
    public void testCanvasText() {
        theInternet.challengingDomSteps()
                .openPage()
                .verifyCanvasText("Expected Text");
    }

    @Test
    @DisplayName("Проверка количества таблиц на странице")
    @Description("Проверяет, что на странице 'Challenging DOM' присутствует ожидаемое количество таблиц.")
    public void testTableCount() {
        theInternet.challengingDomSteps()
                .openPage()
                .checkTableCount(1);
    }

    @Test
    @DisplayName("Проверка полной загрузки страницы")
    @Description("Проверяет, что страница 'Challenging DOM' загружается полностью и без ошибок.")
    public void testPageLoad() {
        theInternet.challengingDomSteps()
                .openPage()
                .checkPageIsLoaded();
    }
}
