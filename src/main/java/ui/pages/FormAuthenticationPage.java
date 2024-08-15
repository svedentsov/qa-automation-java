package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;
import ui.widgets.Input;

@Url(pattern = ".*/login")
public class FormAuthenticationPage extends AbstractPage<FormAuthenticationPage> {
    public Input USERNAME_INPUT = new Input(By.xpath("//input[@id='username']"));
    public Input PASSWORD_INPUT = new Input(By.xpath("//input[@id='password']"));
    public Button ALERT_MESSAGE = new Button(By.xpath("//div[@id='flash']"));
    public Button SUBMIT_FORM_BUTTON = new Button(By.xpath("//form[@id='login']//button[@type='submit']"));
    public Button LOGOUT_BUTTON = new Button(By.xpath("//a[@href='/logout']"));
}
