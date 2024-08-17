package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.CheckBox;

/**
 * Класс предоставляет элементы страницы Checkboxes.
 * Содержит элемент для взаимодействия с чекбоксами.
 */
@Url(pattern = ".*/checkboxes")
public class CheckboxesPage extends AbstractPage<CheckboxesPage> {
    public CheckBox CHECKBOX = new CheckBox(By.xpath("//form[@id='checkboxes']//input"));
}
