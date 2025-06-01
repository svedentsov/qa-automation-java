package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы JavaScript Error.
 * Содержит элемент для проверки наличия сообщений об ошибках JavaScript.
 */
@Url(pattern = ".*/javascript_error")
public class JavaScriptErrorPage extends AbstractPage<JavaScriptErrorPage> {
    public LineRead ERROR_MESSAGE_TEXT = new LineRead(By.xpath("//p[contains(text(),'There is an error on this page')]"));
}
