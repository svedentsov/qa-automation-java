package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Frame;
import ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Nested Frames.
 * Содержит элементы для работы с вложенными фреймами и контентом внутри них.
 */
@Url(pattern = ".*/nested_frames")
public class NestedFramesPage extends AbstractPage<NestedFramesPage> {
    public Frame FRAME_TOP = new Frame(By.name("frame-top"));
    public Frame FRAME_BOTTOM = new Frame(By.name("frame-bottom"));
    public Frame FRAME_LEFT = new Frame(By.name("frame-left"));
    public Frame FRAME_MIDDLE = new Frame(By.name("frame-middle"));
    public Frame FRAME_RIGHT = new Frame(By.name("frame-right"));

    public LineRead MIDDLE_FRAME_TEXT = new LineRead(By.xpath("//body[contains(text(),'MIDDLE')]"));

    public LineRead BOTTOM_FRAME_TEXT = new LineRead(By.xpath("//body[contains(text(),'BOTTOM')]"));
}
