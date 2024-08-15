package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Checkbox;

@Url(pattern = ".*/checkboxes")
public class CheckboxesPage extends AbstractPage<CheckboxesPage> {
    public Checkbox CHECKBOX = new Checkbox(By.xpath("//form[@id='checkboxes']//input"));
}
