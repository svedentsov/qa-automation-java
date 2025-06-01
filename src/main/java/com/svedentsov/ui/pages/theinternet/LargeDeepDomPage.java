package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Large & Deep DOM.
 * Содержит элементы для взаимодействия с определенными разделами большого DOM-дерева.
 */
@Url(pattern = ".*/large")
public class LargeDeepDomPage extends AbstractPage<LargeDeepDomPage> {
    public LineRead ELEMENT_HEADER = new LineRead(By.xpath("//h3[text()='Large & Deep DOM']"));
    public LineRead TARGET_ELEMENT = new LineRead(By.xpath("//div[@id='sibling-50.1']")); // Пример: глубокий элемент на странице
}
