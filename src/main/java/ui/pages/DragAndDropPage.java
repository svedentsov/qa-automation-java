package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;

@Url(pattern = ".*/drag_and_drop")
public class DragAndDropPage extends AbstractPage<DragAndDropPage> {
    public Button A_CONTAINER_BUTTON = new Button(By.xpath("//div[@id='column-a']"));
    public Button B_CONTAINER_BUTTON = new Button(By.xpath("//div[@id='column-b']"));
}
