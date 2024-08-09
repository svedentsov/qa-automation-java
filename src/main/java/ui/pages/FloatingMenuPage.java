package ui.pages;

import org.openqa.selenium.By;
import ui.widgets.Button;
import ui.widgets.Text;

public class FloatingMenuPage {

    public Text testTitleLocator = new Text(By.xpath("//h3[normalize-space()='Floating Menu']"));
    public Button floatingMenu = new Button(By.xpath("//div[@id='menu']"));
}
