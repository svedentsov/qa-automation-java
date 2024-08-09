package ui.pages;

import org.openqa.selenium.By;
import ui.widgets.Button;
import ui.widgets.Input;

public class FormAuthenticationPage {

    public Input usernameInput = new Input(By.xpath("//input[@id='username']"));
    public Input passwordInput = new Input(By.xpath("//input[@id='password']"));
    public Button alertMessage = new Button(By.xpath("//div[@id='flash']"));
    public Button submitFormButton = new Button(By.xpath("//form[@id='login']//button[@type='submit']"));
    public Button logoutButton = new Button(By.xpath("//a[@href='/logout']"));
}
