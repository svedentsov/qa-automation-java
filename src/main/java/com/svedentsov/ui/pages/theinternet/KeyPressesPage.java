package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.InputLine;
import com.svedentsov.ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Key Presses.
 * Содержит текстовое поле для ввода и элемент для отображения информации о нажатой клавише.
 */
@Url(pattern = ".*/key_presses")
public class KeyPressesPage extends AbstractPage<KeyPressesPage> {
    public InputLine INPUT_FIELD = new InputLine(By.id("target"));
    public LineRead RESULT_TEXT = new LineRead(By.id("result"));
}
