package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы A/B Testing.
 * Содержит заголовок страницы и описание эксперимента.
 */
@Url(pattern = ".*/abtest")
public class AbTestPage extends AbstractPage<AbTestPage> {
    public LineRead TITLE_TEXT = new LineRead(By.xpath("//h3[contains(text(),'A/B Test Control')]"));
    public LineRead DESCRIPTION_TEXT = new LineRead(By.xpath("//p[contains(text(),'Also known as split testing')]"));
}
