package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;
import ui.widgets.Text;

@Url(pattern = ".*/dynamic_loading")
public class DynamicLoadingPage extends AbstractPage<DynamicLoadingPage> {
    public Button SHOW_ELEMENT_BUTTON = new Button(By.xpath("//div[@id='start']/button"));
    public Text HIDDEN_ELEMENT = new Text(By.xpath("//div[@id='finish']/h4[text()='Hello World!']"));
}
