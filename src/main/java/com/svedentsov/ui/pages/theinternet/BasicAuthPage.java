package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы "Basic Auth".
 */
@Url(pattern = ".*/basic_auth")
public class BasicAuthPage extends AbstractPage<BasicAuthPage> {
    public LineRead BASIC_AUTH_LINK = new LineRead(By.xpath("//h3[text()='Basic Auth']"));
    public LineRead AUTH_SUCCESS_MESSAGE = new LineRead(By.xpath("//p[contains(text(),'Congratulations!')]"));
}
