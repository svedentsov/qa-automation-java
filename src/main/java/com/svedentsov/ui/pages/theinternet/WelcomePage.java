package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Button;

import static com.svedentsov.app.theinternet.enums.MenuItem.*;

/**
 * Класс для представления страницы приветствия веб-приложения.
 * Содержит кнопки для навигации к различным страницам.
 */
@Url(pattern = ".*/#")
public class WelcomePage extends AbstractPage<WelcomePage> {
    public Button AB_TEST_LINK = new Button(By.linkText(AB_TEST.displayName()));
    public Button ADD_REMOVE_ELEMENTS_LINK = new Button(By.linkText(ADD_REMOVE_ELEMENTS.displayName()));
    public Button BASIC_AUTH_LINK = new Button(By.linkText(BASIC_AUTH.displayName()));
    public Button BROKEN_IMAGES_LINK = new Button(By.linkText(BROKEN_IMAGES.displayName()));
    public Button CHALLENGING_DOM_LINK = new Button(By.linkText(CHALLENGING_DOM.displayName()));
    public Button CHECKBOXES_LINK = new Button(By.linkText(CHECKBOXES.displayName()));
    public Button CONTEXT_MENU_LINK = new Button(By.linkText(CONTEXT_MENU.displayName()));
    public Button DIGEST_AUTH_LINK = new Button(By.linkText(DIGEST_AUTHENTICATION.displayName()));
    public Button DRAG_AND_DROP_LINK = new Button(By.linkText(DRAG_AND_DROP.displayName()));
    public Button DROPDOWN_LINK = new Button(By.linkText(DROPDOWN.displayName()));
    public Button DYNAMIC_CONTENT_LINK = new Button(By.linkText(DYNAMIC_CONTENT.displayName()));
    public Button DYNAMIC_CONTROLS_LINK = new Button(By.linkText(DYNAMIC_CONTROLS.displayName()));
    public Button DYNAMIC_LOADING_LINK = new Button(By.linkText(DYNAMIC_LOADING.displayName()));
    public Button ENTRY_AD_LINK = new Button(By.linkText(ENTRY_AD.displayName()));
    public Button EXIT_INTENT_LINK = new Button(By.linkText(EXIT_INTENT.displayName()));
    public Button FILE_DOWNLOAD_LINK = new Button(By.linkText(FILE_DOWNLOAD.displayName()));
    public Button FILE_UPLOAD_LINK = new Button(By.linkText(FILE_UPLOAD.displayName()));
    public Button FLOATING_MENU_LINK = new Button(By.linkText(FLOATING_MENU.displayName()));
    public Button FORGOT_PASSWORD_LINK = new Button(By.linkText(FORGOT_PASSWORD.displayName()));
    public Button FORM_AUTHENTICATION_LINK = new Button(By.linkText(FORM_AUTHENTICATION.displayName()));
    public Button FRAMES_LINK = new Button(By.linkText(FRAMES.displayName()));
    public Button GEOLOCATION_LINK = new Button(By.linkText(GEOLOCATION.displayName()));
    public Button HORIZONTAL_SLIDER_LINK = new Button(By.linkText(HORIZONTAL_SLIDER.displayName()));
    public Button HOVERS_LINK = new Button(By.linkText(HOVERS.displayName()));
    public Button INFINITE_SCROLL_LINK = new Button(By.linkText(INFINITE_SCROLL.displayName()));
    public Button JAVASCRIPT_ALERTS_LINK = new Button(By.linkText(JAVASCRIPT_ALERTS.displayName()));
    public Button JAVASCRIPT_ERROR_LINK = new Button(By.linkText(JAVASCRIPT_ONLOAD_EVENT_ERROR.displayName()));
    public Button KEY_PRESS_LINK = new Button(By.linkText(KEY_PRESSES.displayName()));
    public Button LARGE_DEEP_DOM_LINK = new Button(By.linkText(LARGE_DEEP_DOM.displayName()));
    public Button MULTIPLE_WINDOWS_LINK = new Button(By.linkText(MULTIPLE_WINDOWS.displayName()));
    public Button NESTED_FRAMES_LINK = new Button(By.linkText(NESTED_FRAMES.displayName()));
    public Button NOTIFICATION_MESSAGES_LINK = new Button(By.linkText(NOTIFICATION_MESSAGES.displayName()));
    public Button REDIRECT_LINK = new Button(By.linkText(REDIRECT.displayName()));
    public Button SECURE_FILE_DOWNLOAD_LINK = new Button(By.linkText(SECURE_FILE_DOWNLOAD.displayName()));
    public Button SHADOW_DOM_LINK = new Button(By.linkText(SHADOW_DOM.displayName()));
    public Button SHIFTING_CONTENT_LINK = new Button(By.linkText(SHIFTING_CONTENT.displayName()));
    public Button SORTABLE_DATA_TABLES_LINK = new Button(By.linkText(SORTABLE_DATA_TABLES.displayName()));
    public Button STATUS_CODES_LINK = new Button(By.linkText(STATUS_CODES.displayName()));
    public Button TYPOS_LINK = new Button(By.linkText(TYPOS.displayName()));
    public Button WYSIWYG_EDITOR_LINK = new Button(By.linkText(WYSIWYG_EDITOR.displayName()));
}
