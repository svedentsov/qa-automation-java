package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Button;
import com.svedentsov.ui.element.InputLine;

/**
 * Класс предоставляет элементы страницы Form Authentication.
 * Содержит элементы для ввода данных в форму аутентификации, отображения сообщений и управления сессией.
 */
@Url(pattern = ".*/login")
public class FormAuthenticationPage extends AbstractPage<FormAuthenticationPage> {
    public InputLine USERNAME_INPUT = new InputLine(By.xpath("//input[@id='username']"));
    public InputLine PASSWORD_INPUT = new InputLine(By.xpath("//input[@id='password']"));
    public Button ALERT_MESSAGE = new Button(By.xpath("//div[@id='flash']"));
    public Button SUBMIT_FORM_BUTTON = new Button(By.xpath("//form[@id='login']//button[@type='submit']"));
    public Button LOGOUT_BUTTON = new Button(By.xpath("//a[@href='/logout']"));
}
