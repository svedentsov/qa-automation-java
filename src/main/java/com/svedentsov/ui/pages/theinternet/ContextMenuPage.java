package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Button;

/**
 * Класс предоставляет элементы страницы Context Menu.
 * Содержит элемент для взаимодействия с контекстным меню.
 */
@Url(pattern = ".*/context_menu")
public class ContextMenuPage extends AbstractPage<ContextMenuPage> {
    public Button RIGHT_CLICK_BOX = new Button(By.xpath("//div[@id='hot-spot']"));
}
