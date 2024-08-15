package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;

@Url(pattern = ".*/exit_intent")
public class ExitIntentPage extends AbstractPage<ExitIntentPage> {
    public Button MOUSE_MOVE_MODAL = new Button(By.xpath("//div[@id='ouibounce-modal']"));
}
