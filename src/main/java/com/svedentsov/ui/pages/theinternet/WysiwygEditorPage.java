package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Button;
import com.svedentsov.ui.element.TextArea;

/**
 * Класс предоставляет элементы страницы WYSIWYG Editor.
 * Содержит элементы для взаимодействия с WYSIWYG редактором и проверки его содержимого.
 */
@Url(pattern = ".*/tinymce")
public class WysiwygEditorPage extends AbstractPage<WysiwygEditorPage> {
    public TextArea EDITOR_TEXTAREA = new TextArea(By.id("tinymce"));
    public Button BOLD_BUTTON = new Button(By.cssSelector("button[aria-label='Bold']"));
}
