package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Link;

/**
 * Класс предоставляет элементы страницы Redirect Link.
 * Содержит ссылку для перенаправления на другую страницу.
 */
@Url(pattern = ".*/redirector")
public class RedirectLinkPage extends AbstractPage<RedirectLinkPage> {
    public Link REDIRECT_LINK = new Link(By.id("redirect"));
}
