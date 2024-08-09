package ui.pages;

import org.openqa.selenium.By;
import ui.widgets.Select;

public class DropdownPage {

    public Select select = new Select(By.xpath("//select[@id='dropdown']"));
}
