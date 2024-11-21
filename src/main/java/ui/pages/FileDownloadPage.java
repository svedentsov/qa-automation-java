package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Link;

/**
 * Класс предоставляет элементы страницы File Download.
 * Содержит ссылки для скачивания файлов.
 */
@Url(pattern = ".*/download")
public class FileDownloadPage extends AbstractPage<FileDownloadPage> {
    public Link FILE_LINK = new Link(By.xpath("//div[@id='content']//a[contains(text(),'some-file.txt')]"));
}
