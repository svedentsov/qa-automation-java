package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Typos.
 * Содержит текстовые элементы для проверки наличия опечаток или текстовых ошибок.
 */
@Url(pattern = ".*/typos")
public class TyposPage extends AbstractPage<TyposPage> {
    public LineRead TYPO_TEXT_1 = new LineRead(By.xpath("//p[contains(text(), 'This example')]"));
    public LineRead TYPO_TEXT_2 = new LineRead(By.xpath("//p[contains(text(), 'The examples')]"));
}
