package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.LineRead;

@Url(pattern = ".*/basic_auth")
public class BasicAuthPage extends AbstractPage<BasicAuthPage> {
    public LineRead BASIC_AUTH_LINK = new LineRead(By.xpath("//a[text()='Basic Auth']"));
    public LineRead AUTH_SUCCESS_MESSAGE = new LineRead(By.xpath("//p[contains(text(),'Congratulations! You must have the proper credenti')]"));
}
