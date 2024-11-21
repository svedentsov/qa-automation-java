package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Button;

/**
 * Класс предоставляет элементы страницы Entry Ad.
 * Содержит элементы для взаимодействия с модальным окном и кнопками рекламы.
 */
@Url(pattern = ".*/entry_ad")
public class EntryAdPage extends AbstractPage<EntryAdPage> {
    public Button MODAL_BUTTON = new Button(By.xpath("//div[@id='modal']//p[text()='Close']"));
    public Button RESTART_AD_BUTTON = new Button(By.xpath("//a[@id='restart-ad']"));
}
