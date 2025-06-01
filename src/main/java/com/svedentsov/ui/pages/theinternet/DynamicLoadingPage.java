package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Button;
import com.svedentsov.ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Dynamic Loading.
 * Содержит элементы для взаимодействия с динамически загружаемыми элементами.
 */
@Url(pattern = ".*/dynamic_loading")
public class DynamicLoadingPage extends AbstractPage<DynamicLoadingPage> {
    public Button SHOW_ELEMENT_BUTTON = new Button(By.xpath("//div[@id='start']/button"));
    public LineRead HIDDEN_ELEMENT = new LineRead(By.xpath("//div[@id='finish']/h4[text()='Hello World!']"));
}
