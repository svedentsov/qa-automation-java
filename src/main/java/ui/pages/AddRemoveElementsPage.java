package ui.pages;

import org.openqa.selenium.By;
import ui.widgets.Button;
import ui.widgets.Text;

/**
 * Класс AddRemoveElementsPage предоставляет элементы страницы "Add/Remove Elements".
 * Содержит элементы для взаимодействия с кнопками добавления и удаления, а также заголовок страницы.
 */
public class AddRemoveElementsPage {

    public Text TITLE_TEXT = new Text(By.xpath("//h3[contains(text(),'Add/Remove Elements')]"));
    public Button ADD_BUTTON = new Button(By.xpath("//button[text()='Add Element']"));
    public Button REMOVE_BUTTON = new Button(By.xpath("//button[text()='Delete']"));
}
