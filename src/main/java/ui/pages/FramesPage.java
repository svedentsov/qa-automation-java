package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Frame;

/**
 * Класс предоставляет элементы страницы Frames.
 * Содержит элементы для работы с фреймами, такими как iFrame и Nested Frames.
 */
@Url(pattern = ".*/frames")
public class FramesPage extends AbstractPage<FramesPage> {
    public Frame IFRAME = new Frame(By.xpath("//iframe[@id='mce_0_ifr']"));
    public Frame NESTED_FRAMES_TOP = new Frame(By.xpath("//frame[@name='frame-top']"));
    public Frame NESTED_FRAMES_BOTTOM = new Frame(By.xpath("//frame[@name='frame-bottom']"));
}
