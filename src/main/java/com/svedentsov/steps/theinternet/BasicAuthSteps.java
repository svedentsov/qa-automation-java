package com.svedentsov.steps.theinternet;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.open;

/**
 * Класс предоставляет шаги для взаимодействия со страницей "Basic Auth."
 */
public class BasicAuthSteps extends BaseSteps {

    /**
     * Открывает страницу "Basic Auth" с предоставленными учетными данными.
     *
     * @param username имя пользователя
     * @param password пароль
     * @return текущий объект BasicAuthSteps для цепочного вызова методов
     */
    @Step("Открыть страницу 'Basic Auth' с логином '{username}' и паролем '{password}'")
    public BasicAuthSteps openPageWithAuth(String username, String password) {
        open("https://" + username + ":" + password + "@the-internet.herokuapp.com/basic_auth");
        return this;
    }

    /**
     * Проверяет, что сообщение об успешной авторизации отображается.
     *
     * @return текущий объект BasicAuthSteps для цепочного вызова методов
     */
    @Step("Проверить, что сообщение об успешной авторизации отображается")
    public BasicAuthSteps checkSeeAuthSuccessMessage() {
        ui.basicAuthPage().AUTH_SUCCESS_MESSAGE.shouldBeVisible();
        return this;
    }

    /**
     * Проверяет, что ссылка на "Basic Auth" отображается на странице.
     *
     * @return текущий объект BasicAuthSteps для цепочного вызова методов
     */
    @Step("Проверить, что ссылка на 'Basic Auth' отображается на странице")
    public BasicAuthSteps checkSeeBasicAuthLink() {
        ui.basicAuthPage().BASIC_AUTH_LINK.shouldBeVisible();
        return this;
    }

    /**
     * Проверяет, что сообщение об успешной авторизации содержит правильный текст.
     *
     * @param expectedMessage ожидаемый текст сообщения
     * @return текущий объект BasicAuthSteps для цепочного вызова методов
     */
    @Step("Проверить, что сообщение об успешной авторизации содержит текст '{expectedMessage}'")
    public BasicAuthSteps checkAuthSuccessMessageText(String expectedMessage) {
        ui.basicAuthPage().AUTH_SUCCESS_MESSAGE.checkText(expectedMessage);
        return this;
    }
}
