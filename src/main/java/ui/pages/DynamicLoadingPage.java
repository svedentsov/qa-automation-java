package ui.pages;

import org.openqa.selenium.By;
import ui.widgets.Button;
import ui.widgets.Text;

public class DynamicLoadingPage {

    public Button showElementBtn = new Button(By.xpath("//div[@id='start']/button"));
    public Text hiddenElement = new Text(By.xpath("//div[@id='finish']/h4[text()='Hello World!']"));
}
