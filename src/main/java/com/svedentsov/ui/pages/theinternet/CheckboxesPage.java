package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.CheckBox;

/**
 * Класс предоставляет элементы страницы Checkboxes.
 * Содержит элемент для взаимодействия с чекбоксами.
 */
@Url(pattern = ".*/checkboxes")
public class CheckboxesPage extends AbstractPage<CheckboxesPage> {
    public CheckBox CHECKBOX = new CheckBox(By.xpath("//form[@id='checkboxes']//input"));
}
