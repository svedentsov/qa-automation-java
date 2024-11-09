package ui.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Перечисление, представляющее элементы меню.
 * Каждый элемент содержит отображаемое имя, которое используется для идентификации на пользовательском интерфейсе.
 */
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum MenuItems {
    AB_TEST("A/B Testing"),
    ADD_REMOVE_ELEMENTS("Add/Remove Elements"),
    BASIC_AUTH("Basic Auth"),
    BROKEN_IMAGES("Broken Images"),
    CHALLENGING_DOM("Challenging DOM"),
    CHECKBOXES("Checkboxes"),
    CONTEXT_MENU("Context Menu"),
    DIGEST_AUTHENTICATION("Digest Authentication"),
    DISAPPEARING_ELEMENTS("Disappearing Elements"),
    DRAG_AND_DROP("Drag and Drop"),
    DROPDOWN("Dropdown"),
    DYNAMIC_CONTENT("Dynamic Content"),
    DYNAMIC_CONTROLS("Dynamic Controls"),
    DYNAMIC_LOADING("Dynamic Loading"),
    ENTRY_AD("Entry Ad"),
    EXIT_INTENT("Exit Intent"),
    FILE_DOWNLOAD("File Download"),
    FILE_UPLOAD("File Upload"),
    FLOATING_MENU("Floating Menu"),
    FORGOT_PASSWORD("Forgot Password"),
    FORM_AUTHENTICATION("Form Authentication"),
    FRAMES("Frames"),
    GEOLOCATION("Geolocation"),
    HORIZONTAL_SLIDER("Horizontal Slider"),
    HOVERS("Hovers"),
    INFINITE_SCROLL("Infinite Scroll"),
    INPUTS("Inputs"),
    JQUERY_UI_MENUS("JQuery UI Menus"),
    JAVASCRIPT_ALERTS("JavaScript Alerts"),
    JAVASCRIPT_ONLOAD_EVENT_ERROR("JavaScript onload event error"),
    KEY_PRESSES("Key Presses"),
    LARGE_DEEP_DOM("Large & Deep DOM"),
    MULTIPLE_WINDOWS("Multiple Windows"),
    NESTED_FRAMES("Nested Frames"),
    NOTIFICATION_MESSAGES("Notification Messages"),
    REDIRECT("Redirect Link"),
    SECURE_FILE_DOWNLOAD("Secure File Download"),
    SHADOW_DOM("Shadow DOM"),
    SHIFTING_CONTENT("Shifting Content"),
    SLOW_RESOURCES("Slow Resources"),
    SORTABLE_DATA_TABLES("Sortable Data Tables"),
    STATUS_CODES("Status Codes"),
    TYPOS("Typos"),
    WYSIWYG_EDITOR("WYSIWYG Editor");

    private final String displayName;
}
