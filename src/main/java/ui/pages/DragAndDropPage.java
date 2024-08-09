package ui.pages;

import org.openqa.selenium.By;
import ui.widgets.Button;

public class DragAndDropPage {

    public Button aContainer = new Button(By.xpath("//div[@id='column-a']"));
    public Button bContainer = new Button(By.xpath("//div[@id='column-b']"));
}
