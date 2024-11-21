package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.LineRead;
import ui.element.partials.Block;

/**
 * Класс предоставляет элементы страницы Shadow DOM.
 * Содержит элементы для работы с теневыми корнями и содержимым внутри них.
 */
@Url(pattern = ".*/shadowdom")
public class ShadowDomPage extends AbstractPage<ShadowDomPage> {
    // Пример теневого корня
    public Block SHADOW_ROOT = new Block(By.cssSelector("#shadow-host"));
    // Пример элементов внутри теневого корня
    public LineRead SHADOW_TEXT = new LineRead(By.cssSelector("p.shadow-text"));
}
