package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Infinite Scroll.
 * Содержит элементы для проверки наличия элементов и взаимодействие с бесконечной прокруткой.
 */
@Url(pattern = ".*/infinite_scroll")
public class InfiniteScrollPage extends AbstractPage<InfiniteScrollPage> {
    public LineRead SCROLLABLE_CONTENT = new LineRead(By.xpath("//div[@id='content']"));
    public LineRead SCROLL_ITEM = new LineRead(By.xpath("//div[@class='scrollable']/div[contains(@class, 'jscroll-added')]"));
}
