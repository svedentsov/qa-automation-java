package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Button;

/**
 * Класс предоставляет элементы страницы Entry Ad.
 * Содержит элементы для взаимодействия с модальным окном и кнопками рекламы.
 */
@Url(pattern = ".*/entry_ad")
public class EntryAdPage extends AbstractPage<EntryAdPage> {
    public Button MODAL_BUTTON = new Button(By.xpath("//div[@id='modal']//p[text()='Close']"));
    public Button RESTART_AD_BUTTON = new Button(By.xpath("//a[@id='restart-ad']"));
}
