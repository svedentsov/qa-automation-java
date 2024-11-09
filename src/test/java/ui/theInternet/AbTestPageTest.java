package ui.theInternet;

import common.UITest;
import core.annotations.Layer;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@Layer("ui")
@Feature("Тестирование функциональности A/B Testing")
@DisplayName("Тесты страницы 'A/B Testing'")
public class AbTestPageTest extends UITest {

    @BeforeEach
    public void setUp() {
        open("https://the-internet.herokuapp.com");
        theInternet.welcomePageSteps()
                .abTestClick();
    }

    @Test
    @Story("Проверка заголовка страницы")
    @DisplayName("Проверка отображения заголовка страницы 'A/B Test Control'")
    public void checkPageTitle() {
        theInternet.abTestSteps()
                .checkTitleIsVisible()
                .checkTitleText("A/B Test Control");
    }

    @Test
    @Story("Проверка описания страницы")
    @DisplayName("Проверка отображения описания страницы")
    public void checkPageDescription() {
        theInternet.abTestSteps()
                .checkDescriptionIsVisible()
                .checkDescriptionText("Also known as split testing");
    }
}
