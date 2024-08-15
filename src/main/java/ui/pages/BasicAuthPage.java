package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Text;

@Url(pattern = ".*/basic_auth")
public class BasicAuthPage extends AbstractPage<BasicAuthPage> {
    public Text BASIC_AUTH_LINK = new Text(By.xpath("//a[text()='Basic Auth']"));
    public Text AUTH_SUCCESS_MESSAGE = new Text(By.xpath("//p[contains(text(),'Congratulations! You must have the proper credenti')]"));
}
