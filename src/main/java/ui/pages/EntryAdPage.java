package ui.pages;

import org.openqa.selenium.By;
import ui.widgets.Button;

public class EntryAdPage {

    public Button modalBtn = new Button(By.xpath("//div[@id='modal']//p[text()='Close']"));
    public Button restartAdBtn = new Button(By.xpath("//a[@id='restart-ad']"));
}
