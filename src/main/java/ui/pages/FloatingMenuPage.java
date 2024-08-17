package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;
import ui.widgets.LineRead;

/**
 * Класс предоставляет элементы страницы Floating Menu.
 * Содержит элементы для взаимодействия с плавающим меню и заголовком теста.
 */
@Url(pattern = ".*/floating_menu")
public class FloatingMenuPage extends AbstractPage<FloatingMenuPage> {
    public LineRead TEST_TITLE_LOCATOR = new LineRead(By.xpath("//h3[normalize-space()='Floating Menu']"));
    public Button FLOATING_MENU = new Button(By.xpath("//div[@id='menu']"));
}
