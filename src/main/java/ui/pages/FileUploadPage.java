package ui.pages;

import org.openqa.selenium.By;
import ui.widgets.Button;

public class FileUploadPage {

    public Button uploadInput = new Button(By.xpath("//input[@id='file-upload']"));
    public Button uploadSubmit = new Button(By.xpath("//input[@id='file-submit']"));
    public Button uploadedFileContainer = new Button(By.xpath("//div[@id='uploaded-files']"));
}
