package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Button;
import ui.element.LineRead;
import ui.element.Table;

/**
 * Класс предоставляет элементы страницы Challenging DOM.
 * Содержит кнопки различных цветов и элемент холста для взаимодействия.
 */
@Url(pattern = ".*/challenging_dom")
public class ChallengingDomPage extends AbstractPage<ChallengingDomPage> {
    public Button BLUE_BUTTON = new Button(By.xpath("//a[@class='button']"));
    public Button RED_BUTTON = new Button(By.xpath("//a[contains(@class, 'alert')]"));
    public Button GREEN_BUTTON = new Button(By.xpath("//a[contains(@class, 'success')]"));
    public LineRead CANVAS_WITH_TEXT = new LineRead(By.xpath("//canvas[@id='canvas']"));
    public Table TABLE = new Table(By.xpath("//table"));
}
