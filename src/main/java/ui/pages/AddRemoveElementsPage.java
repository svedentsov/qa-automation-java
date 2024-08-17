package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;
import ui.widgets.LineRead;

/**
 * Класс AddRemoveElementsPage предоставляет элементы страницы "Add/Remove Elements".
 * Содержит элементы для взаимодействия с кнопками добавления и удаления, а также заголовок страницы.
 */
@Url(pattern = ".*/add_remove_elements/")
public class AddRemoveElementsPage extends AbstractPage<AddRemoveElementsPage> {
    public LineRead TITLE_TEXT = new LineRead(By.xpath("//h3[contains(text(),'Add/Remove Elements')]"));
    public Button ADD_BUTTON = new Button(By.xpath("//button[text()='Add Element']"));
    public Button REMOVE_BUTTON = new Button(By.xpath("//button[text()='Delete']"));
}
