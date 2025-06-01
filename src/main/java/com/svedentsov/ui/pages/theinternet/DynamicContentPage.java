package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Dynamic Content.
 * Содержит текстовые элементы, отображающие динамически изменяющийся контент.
 */
@Url(pattern = ".*/dynamic_content")
public class DynamicContentPage extends AbstractPage<DynamicContentPage> {
    public LineRead DYNAMIC_CONTENT_1 = new LineRead(By.xpath("//div[@id='content']//div[1]//div[@class='large-10 columns']"));
    public LineRead DYNAMIC_CONTENT_2 = new LineRead(By.xpath("//div[@id='content']//div[2]//div[@class='large-10 columns']"));
    public LineRead DYNAMIC_CONTENT_3 = new LineRead(By.xpath("//div[@id='content']//div[3]//div[@class='large-10 columns']"));
}
