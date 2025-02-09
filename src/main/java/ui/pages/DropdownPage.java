package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.element.DropdownList;

/**
 * Класс предоставляет элементы страницы Dropdown.
 * Содержит элемент для работы с выпадающим списком.
 */
@Url(pattern = ".*/dropdown")
public class DropdownPage extends AbstractPage<DropdownPage> {
    public DropdownList SELECT = new DropdownList(By.xpath("//select[@id='dropdown']"));
}
