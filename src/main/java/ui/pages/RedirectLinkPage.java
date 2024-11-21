package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Link;

/**
 * Класс предоставляет элементы страницы Redirect Link.
 * Содержит ссылку для перенаправления на другую страницу.
 */
@Url(pattern = ".*/redirector")
public class RedirectLinkPage extends AbstractPage<RedirectLinkPage> {
    public Link REDIRECT_LINK = new Link(By.id("redirect"));
}
