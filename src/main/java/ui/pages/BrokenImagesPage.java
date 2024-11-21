package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Images;

/**
 * Класс предоставляет элементы страницы Broken Images.
 * Содержит изображения, присутствующие на странице.
 */
@Url(pattern = ".*/broken_images")
public class BrokenImagesPage extends AbstractPage<BrokenImagesPage> {
    public Images IMAGES = new Images(By.xpath("//div[@class='example']/img"));
}
