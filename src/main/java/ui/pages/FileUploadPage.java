package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;

@Url(pattern = ".*/upload")
public class FileUploadPage extends AbstractPage<FileUploadPage> {
    public Button UPLOAD_INPUT = new Button(By.xpath("//input[@id='file-upload']"));
    public Button UPLOAD_SUBMIT = new Button(By.xpath("//input[@id='file-submit']"));
    public Button UPLOADED_FILE_CONTAINER = new Button(By.xpath("//div[@id='uploaded-files']"));
}
