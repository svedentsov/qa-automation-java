package ui.steps;

import io.qameta.allure.Step;

public class WelcomePageSteps extends BaseSteps {

    @Step("Нажать кнопку 'A/B Testing'")
    public WelcomePageSteps abTestClick() {
        pages.welcomePage().AB_TEST_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Add/Remove Elements'")
    public WelcomePageSteps addRemoveElementsClick() {
        pages.welcomePage().ADD_REMOVE_ELEMENTS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Basic Auth'")
    public WelcomePageSteps basicAuthClick() {
        pages.welcomePage().BASIC_AUTH_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Broken Images'")
    public WelcomePageSteps brokenImagesClick() {
        pages.welcomePage().BROKEN_IMAGES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Challenging DOM'")
    public WelcomePageSteps challengingDomClick() {
        pages.welcomePage().CHALLENGING_DOM_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Checkboxes'")
    public WelcomePageSteps checkboxesClick() {
        pages.welcomePage().CHECKBOXES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Context Menu'")
    public WelcomePageSteps contextMenuClick() {
        pages.welcomePage().CONTEXT_MENU_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Digest Authentication'")
    public WelcomePageSteps digestAuthClick() {
        pages.welcomePage().DIGEST_AUTH_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Drag and Drop'")
    public WelcomePageSteps dragAndDropClick() {
        pages.welcomePage().DRAG_AND_DROP_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Dropdown'")
    public WelcomePageSteps dropdownClick() {
        pages.welcomePage().DROPDOWN_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Dynamic Content'")
    public WelcomePageSteps dynamicContentClick() {
        pages.welcomePage().DYNAMIC_CONTENT_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Dynamic Controls'")
    public WelcomePageSteps dynamicControlsClick() {
        pages.welcomePage().DYNAMIC_CONTROLS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Dynamic Loading'")
    public WelcomePageSteps dynamicLoadingClick() {
        pages.welcomePage().DYNAMIC_LOADING_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Entry Ad'")
    public WelcomePageSteps entryAdClick() {
        pages.welcomePage().ENTRY_AD_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Exit Intent'")
    public WelcomePageSteps exitIntentClick() {
        pages.welcomePage().EXIT_INTENT_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'File Download'")
    public WelcomePageSteps fileDownloadClick() {
        pages.welcomePage().FILE_DOWNLOAD_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'File Upload'")
    public WelcomePageSteps fileUploadClick() {
        pages.welcomePage().FILE_UPLOAD_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Floating Menu'")
    public WelcomePageSteps floatingMenuClick() {
        pages.welcomePage().FLOATING_MENU_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Forgot Password'")
    public WelcomePageSteps forgotPasswordClick() {
        pages.welcomePage().FORGOT_PASSWORD_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Form Authentication'")
    public WelcomePageSteps formAuthenticationClick() {
        pages.welcomePage().FORM_AUTHENTICATION_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Frames'")
    public WelcomePageSteps framesClick() {
        pages.welcomePage().FRAMES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Geolocation'")
    public WelcomePageSteps geolocationClick() {
        pages.welcomePage().GEOLOCATION_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Horizontal Slider'")
    public WelcomePageSteps horizontalSliderClick() {
        pages.welcomePage().HORIZONTAL_SLIDER_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Hovers'")
    public WelcomePageSteps hoversClick() {
        pages.welcomePage().HOVERS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Infinite Scroll'")
    public WelcomePageSteps infiniteScrollClick() {
        pages.welcomePage().INFINITE_SCROLL_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'JavaScript Alerts'")
    public WelcomePageSteps javascriptAlertsClick() {
        pages.welcomePage().JAVASCRIPT_ALERTS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'JavaScript Error'")
    public WelcomePageSteps javascriptErrorClick() {
        pages.welcomePage().JAVASCRIPT_ERROR_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Key Presses'")
    public WelcomePageSteps keyPressesClick() {
        pages.welcomePage().KEY_PRESS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Large & Deep DOM'")
    public WelcomePageSteps largeDeepDomClick() {
        pages.welcomePage().LARGE_DEEP_DOM_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Multiple Windows'")
    public WelcomePageSteps multipleWindowsClick() {
        pages.welcomePage().MULTIPLE_WINDOWS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Nested Frames'")
    public WelcomePageSteps nestedFramesClick() {
        pages.welcomePage().NESTED_FRAMES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Notification Messages'")
    public WelcomePageSteps notificationMessagesClick() {
        pages.welcomePage().NOTIFICATION_MESSAGES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Redirect Link'")
    public WelcomePageSteps redirectLinkClick() {
        pages.welcomePage().REDIRECT_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Secure File Download'")
    public WelcomePageSteps secureFileDownloadClick() {
        pages.welcomePage().SECURE_FILE_DOWNLOAD_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Shadow DOM'")
    public WelcomePageSteps shadowDomClick() {
        pages.welcomePage().SHADOW_DOM_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Shifting Content'")
    public WelcomePageSteps shiftingContentClick() {
        pages.welcomePage().SHIFTING_CONTENT_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Sortable Data Tables'")
    public WelcomePageSteps sortableDataTablesClick() {
        pages.welcomePage().SORTABLE_DATA_TABLES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Status Codes'")
    public WelcomePageSteps statusCodesClick() {
        pages.welcomePage().STATUS_CODES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Typos'")
    public WelcomePageSteps typosClick() {
        pages.welcomePage().TYPOS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'WYSIWYG Editor'")
    public WelcomePageSteps wysiwygEditorClick() {
        pages.welcomePage().WYSIWYG_EDITOR_LINK.click();
        return this;
    }
}
