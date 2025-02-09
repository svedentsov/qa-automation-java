package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Button;
import ui.element.InputLine;
import ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Forgot Password.
 * Содержит поле для ввода email и кнопку для отправки запроса на восстановление пароля.
 */
@Url(pattern = ".*/forgot_password")
public class ForgotPasswordPage extends AbstractPage<ForgotPasswordPage> {
    public InputLine EMAIL_INPUT = new InputLine(By.id("email"));
    public Button RETRIEVE_PASSWORD_BUTTON = new Button(By.id("form_submit"));
    public LineRead CONFIRMATION_TEXT = new LineRead(By.id("content"));
}
