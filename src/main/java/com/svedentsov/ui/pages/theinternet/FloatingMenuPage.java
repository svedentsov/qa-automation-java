package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Button;
import com.svedentsov.ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Floating Menu.
 * Содержит элементы для взаимодействия с плавающим меню и заголовком теста.
 */
@Url(pattern = ".*/floating_menu")
public class FloatingMenuPage extends AbstractPage<FloatingMenuPage> {
    public LineRead TEST_TITLE_LOCATOR = new LineRead(By.xpath("//h3[normalize-space()='Floating Menu']"));
    public Button FLOATING_MENU = new Button(By.xpath("//div[@id='menu']"));
}
