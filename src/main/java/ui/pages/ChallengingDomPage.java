package ui.pages;

import org.openqa.selenium.By;
import ui.widgets.Button;

public class ChallengingDomPage {

    public Button blueButton = new Button(By.xpath("//a[@class='button']"));
    public Button redButton = new Button(By.xpath("//a[contains(@class, 'alert')]"));
    public Button greenButton = new Button(By.xpath("//a[contains(@class, 'success')]"));
    public Button canvasWithText = new Button(By.xpath("//canvas[@id='canvas']"));
}
