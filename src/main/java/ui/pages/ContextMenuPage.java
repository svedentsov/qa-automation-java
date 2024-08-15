package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;

@Url(pattern = ".*/context_menu")
public class ContextMenuPage extends AbstractPage<ContextMenuPage> {
    public Button RIGHT_CLICK_BOX = new Button(By.xpath("//div[@id='hot-spot']"));
}
