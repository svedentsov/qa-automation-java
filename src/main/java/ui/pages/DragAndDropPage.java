package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;

/**
 * Класс предоставляет элементы страницы Drag and Drop.
 * Содержит элементы для взаимодействия с контейнерами перетаскивания.
 */
@Url(pattern = ".*/drag_and_drop")
public class DragAndDropPage extends AbstractPage<DragAndDropPage> {
    public Button A_CONTAINER_BUTTON = new Button(By.id("column-a"));
    public Button B_CONTAINER_BUTTON = new Button(By.id("column-b"));
}
