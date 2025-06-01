package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Link;

/**
 * Класс предоставляет элементы страницы Multiple Windows.
 * Содержит ссылку для открытия нового окна или вкладки.
 */
@Url(pattern = ".*/windows")
public class MultipleWindowsPage extends AbstractPage<MultipleWindowsPage> {
    public Link CLICK_HERE_LINK = new Link(By.xpath("//a[text()='Click Here']"));
}
