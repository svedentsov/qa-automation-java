package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Link;

/**
 * Класс предоставляет элементы страницы Secure File Download.
 * Содержит ссылки для скачивания защищенных файлов.
 */
@Url(pattern = ".*/download_secure")
public class SecureFileDownloadPage extends AbstractPage<SecureFileDownloadPage> {
    public Link SECURE_FILE_LINK = new Link(By.xpath("//a[contains(text(), 'some-file.txt')]"));
}
