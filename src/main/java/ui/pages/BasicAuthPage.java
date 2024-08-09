package ui.pages;

import org.openqa.selenium.By;
import ui.widgets.Text;

public class BasicAuthPage {

    public Text basicAuthLink = new Text(By.xpath("//a[text()='Basic Auth']"));
    public Text authSuccessMessage = new Text(By.xpath("//p[contains(text(),'Congratulations! You must have the proper credenti')]"));
}
