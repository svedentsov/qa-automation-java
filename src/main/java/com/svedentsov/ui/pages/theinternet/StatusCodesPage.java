package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Link;
import com.svedentsov.ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Status Codes.
 * Содержит ссылки для проверки различных кодов состояния HTTP и текстовый элемент для отображения результата.
 */
@Url(pattern = ".*/status_codes")
public class StatusCodesPage extends AbstractPage<StatusCodesPage> {

    public Link STATUS_200_LINK = new Link(By.linkText("200"));
    public Link STATUS_301_LINK = new Link(By.linkText("301"));
    public Link STATUS_404_LINK = new Link(By.linkText("404"));
    public Link STATUS_500_LINK = new Link(By.linkText("500"));

    public LineRead RESULT_TEXT = new LineRead(By.id("result"));
}
