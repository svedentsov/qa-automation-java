package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Button;

/**
 * Класс предоставляет элементы страницы File Upload.
 * Содержит элементы для загрузки файлов и отображения загруженных файлов.
 */
@Url(pattern = ".*/upload")
public class FileUploadPage extends AbstractPage<FileUploadPage> {
    public Button UPLOAD_INPUT = new Button(By.xpath("//input[@id='file-upload']"));
    public Button UPLOAD_SUBMIT = new Button(By.xpath("//input[@id='file-submit']"));
    public Button UPLOADED_FILE_CONTAINER = new Button(By.xpath("//div[@id='uploaded-files']"));
}
