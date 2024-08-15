package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;
import ui.widgets.Text;

@Url(pattern = ".*/floating_menu")
public class FloatingMenuPage extends AbstractPage<FloatingMenuPage> {
    public Text TEST_TITLE_LOCATOR = new Text(By.xpath("//h3[normalize-space()='Floating Menu']"));
    public Button FLOATING_MENU = new Button(By.xpath("//div[@id='menu']"));
}
