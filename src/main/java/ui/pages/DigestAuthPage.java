package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Digest Authentication.
 * Содержит заголовок и текст подтверждения успешной аутентификации.
 */
@Url(pattern = ".*/digest_auth")
public class DigestAuthPage extends AbstractPage<DigestAuthPage> {
    public LineRead TITLE_TEXT = new LineRead(By.xpath("//h3[contains(text(),'Digest Auth')]"));
    public LineRead SUCCESS_MESSAGE_TEXT = new LineRead(By.xpath("//p[contains(text(),'Congratulations! You must have the proper credentials.')]"));
}
