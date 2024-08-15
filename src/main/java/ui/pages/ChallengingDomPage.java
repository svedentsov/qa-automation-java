package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;

@Url(pattern = ".*/challenging_dom")
public class ChallengingDomPage extends AbstractPage<ChallengingDomPage> {
    public Button BLUE_BUTTON = new Button(By.xpath("//a[@class='button']"));
    public Button RED_BUTTON = new Button(By.xpath("//a[contains(@class, 'alert')]"));
    public Button GREEN_BUTTON = new Button(By.xpath("//a[contains(@class, 'success')]"));
    public Button CANVAS_WITH_TEXT = new Button(By.xpath("//canvas[@id='canvas']"));
}
