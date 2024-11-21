package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Images;
import ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Hovers.
 * Содержит элементы для взаимодействия с изображениями, с которыми можно взаимодействовать при наведении курсора.
 */
@Url(pattern = ".*/hovers")
public class HoversPage extends AbstractPage<HoversPage> {
    public Images IMAGE_1 = new Images(By.xpath("//div[@class='figure'][1]//img"));
    public LineRead IMAGE_1_CAPTION = new LineRead(By.xpath("//div[@class='figure'][1]//div[@class='figcaption']"));

    public Images IMAGE_2 = new Images(By.xpath("//div[@class='figure'][2]//img"));
    public LineRead IMAGE_2_CAPTION = new LineRead(By.xpath("//div[@class='figure'][2]//div[@class='figcaption']"));

    public Images IMAGE_3 = new Images(By.xpath("//div[@class='figure'][3]//img"));
    public LineRead IMAGE_3_CAPTION = new LineRead(By.xpath("//div[@class='figure'][3]//div[@class='figcaption']"));
}
