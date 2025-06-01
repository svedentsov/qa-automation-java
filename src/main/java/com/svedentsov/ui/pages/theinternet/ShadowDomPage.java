package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.LineRead;
import com.svedentsov.ui.element.partials.Block;

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
