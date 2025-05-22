package ui.theInternet;

import core.UITest;
import core.annotations.Layer;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Layer("UI")
@Feature("Тестирование функционала авторизации")
@DisplayName("Тесты страницы 'Basic Auth'")
public class BasicAuthTests extends UITest {

    @Test
    @Story("Проверка авторизации")
    @DisplayName("Проверка успешной авторизации с отображением сообщений")
    public void successfulAuthorization() {
        theInternet.basicAuthSteps()
                .openPageWithAuth("admin", "admin")
                .checkSeeBasicAuthLink()
                .checkSeeAuthSuccessMessage()
                .checkAuthSuccessMessageText("Congratulations! You must have the proper credentials.");
    }
}
