package ui.pages;

import org.openqa.selenium.By;
import ui.widgets.Checkbox;

public class CheckboxesPage {

    public Checkbox checkboxes = new Checkbox(By.xpath("//form[@id='checkboxes']//input"));
}
