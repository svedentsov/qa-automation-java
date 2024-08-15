package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Image;

@Url(pattern = ".*/broken_images")
public class BrokenImagesPage extends AbstractPage<BrokenImagesPage> {
    public Image IMAGES = new Image(By.xpath("//div[@class='example']/img"));
}
