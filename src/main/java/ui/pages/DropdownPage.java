package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Select;

@Url(pattern = ".*/dropdown")
public class DropdownPage extends AbstractPage<DropdownPage> {
    public Select SELECT = new Select(By.xpath("//select[@id='dropdown']"));
}
