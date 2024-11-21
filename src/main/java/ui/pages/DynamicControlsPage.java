package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Button;
import ui.element.CheckBox;
import ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Dynamic Controls.
 * Содержит элементы для взаимодействия с чекбоксом, кнопками для его управления и индикатором выполнения.
 */
@Url(pattern = ".*/dynamic_controls")
public class DynamicControlsPage extends AbstractPage<DynamicControlsPage> {
    public CheckBox CHECKBOX = new CheckBox(By.xpath("//form[@id='checkbox-example']//input[@type='checkbox']"));
    public Button REMOVE_ADD_BUTTON = new Button(By.xpath("//form[@id='checkbox-example']//button"));
    public LineRead MESSAGE_TEXT = new LineRead(By.id("message"));
    public Button ENABLE_DISABLE_BUTTON = new Button(By.xpath("//form[@id='input-example']//button"));
    public LineRead INPUT_FIELD = new LineRead(By.xpath("//form[@id='input-example']//input[@type='text']"));
}
