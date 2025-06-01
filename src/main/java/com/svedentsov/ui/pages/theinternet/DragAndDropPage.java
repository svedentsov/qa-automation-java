package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Button;

/**
 * Класс предоставляет элементы страницы Drag and Drop.
 * Содержит элементы для взаимодействия с контейнерами перетаскивания.
 */
@Url(pattern = ".*/drag_and_drop")
public class DragAndDropPage extends AbstractPage<DragAndDropPage> {
    public Button A_CONTAINER_BUTTON = new Button(By.id("column-a"));
    public Button B_CONTAINER_BUTTON = new Button(By.id("column-b"));
}
