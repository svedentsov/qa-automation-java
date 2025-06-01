package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Link;

/**
 * Класс предоставляет элементы страницы Secure File Download.
 * Содержит ссылки для скачивания защищенных файлов.
 */
@Url(pattern = ".*/download_secure")
public class SecureFileDownloadPage extends AbstractPage<SecureFileDownloadPage> {
    public Link SECURE_FILE_LINK = new Link(By.xpath("//a[contains(text(), 'some-file.txt')]"));
}
