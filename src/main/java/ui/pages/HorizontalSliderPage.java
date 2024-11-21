package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Slider;
import ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Horizontal Slider.
 * Содержит элементы для взаимодействия с горизонтальным слайдером и отображаемым значением.
 */
@Url(pattern = ".*/horizontal_slider")
public class HorizontalSliderPage extends AbstractPage<HorizontalSliderPage> {
    public Slider SLIDER = new Slider(By.xpath("//input[@type='range']"));
    public LineRead SLIDER_VALUE_TEXT = new LineRead(By.id("range"));
}
