package ui.steps;

import io.qameta.allure.Step;

public class WelcomePageSteps extends BaseSteps {

    @Step("Нажать кнопку 'A/B Testing'")
    public WelcomePageSteps abTestClick() {
        ui.welcomePage().AB_TEST_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Add/Remove Elements'")
    public WelcomePageSteps addRemoveElementsClick() {
        ui.welcomePage().ADD_REMOVE_ELEMENTS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Basic Auth'")
    public WelcomePageSteps basicAuthClick() {
        ui.welcomePage().BASIC_AUTH_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Broken Images'")
    public WelcomePageSteps brokenImagesClick() {
        ui.welcomePage().BROKEN_IMAGES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Challenging DOM'")
    public WelcomePageSteps challengingDomClick() {
        ui.welcomePage().CHALLENGING_DOM_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Checkboxes'")
    public WelcomePageSteps checkboxesClick() {
        ui.welcomePage().CHECKBOXES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Context Menu'")
    public WelcomePageSteps contextMenuClick() {
        ui.welcomePage().CONTEXT_MENU_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Digest Authentication'")
    public WelcomePageSteps digestAuthClick() {
        ui.welcomePage().DIGEST_AUTH_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Drag and Drop'")
    public WelcomePageSteps dragAndDropClick() {
        ui.welcomePage().DRAG_AND_DROP_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Dropdown'")
    public WelcomePageSteps dropdownClick() {
        ui.welcomePage().DROPDOWN_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Dynamic Content'")
    public WelcomePageSteps dynamicContentClick() {
        ui.welcomePage().DYNAMIC_CONTENT_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Dynamic Controls'")
    public WelcomePageSteps dynamicControlsClick() {
        ui.welcomePage().DYNAMIC_CONTROLS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Dynamic Loading'")
    public WelcomePageSteps dynamicLoadingClick() {
        ui.welcomePage().DYNAMIC_LOADING_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Entry Ad'")
    public WelcomePageSteps entryAdClick() {
        ui.welcomePage().ENTRY_AD_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Exit Intent'")
    public WelcomePageSteps exitIntentClick() {
        ui.welcomePage().EXIT_INTENT_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'File Download'")
    public WelcomePageSteps fileDownloadClick() {
        ui.welcomePage().FILE_DOWNLOAD_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'File Upload'")
    public WelcomePageSteps fileUploadClick() {
        ui.welcomePage().FILE_UPLOAD_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Floating Menu'")
    public WelcomePageSteps floatingMenuClick() {
        ui.welcomePage().FLOATING_MENU_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Forgot Password'")
    public WelcomePageSteps forgotPasswordClick() {
        ui.welcomePage().FORGOT_PASSWORD_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Form Authentication'")
    public WelcomePageSteps formAuthenticationClick() {
        ui.welcomePage().FORM_AUTHENTICATION_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Frames'")
    public WelcomePageSteps framesClick() {
        ui.welcomePage().FRAMES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Geolocation'")
    public WelcomePageSteps geolocationClick() {
        ui.welcomePage().GEOLOCATION_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Horizontal Slider'")
    public WelcomePageSteps horizontalSliderClick() {
        ui.welcomePage().HORIZONTAL_SLIDER_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Hovers'")
    public WelcomePageSteps hoversClick() {
        ui.welcomePage().HOVERS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Infinite Scroll'")
    public WelcomePageSteps infiniteScrollClick() {
        ui.welcomePage().INFINITE_SCROLL_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'JavaScript Alerts'")
    public WelcomePageSteps javascriptAlertsClick() {
        ui.welcomePage().JAVASCRIPT_ALERTS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'JavaScript Error'")
    public WelcomePageSteps javascriptErrorClick() {
        ui.welcomePage().JAVASCRIPT_ERROR_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Key Presses'")
    public WelcomePageSteps keyPressesClick() {
        ui.welcomePage().KEY_PRESS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Large & Deep DOM'")
    public WelcomePageSteps largeDeepDomClick() {
        ui.welcomePage().LARGE_DEEP_DOM_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Multiple Windows'")
    public WelcomePageSteps multipleWindowsClick() {
        ui.welcomePage().MULTIPLE_WINDOWS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Nested Frames'")
    public WelcomePageSteps nestedFramesClick() {
        ui.welcomePage().NESTED_FRAMES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Notification Messages'")
    public WelcomePageSteps notificationMessagesClick() {
        ui.welcomePage().NOTIFICATION_MESSAGES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Redirect Link'")
    public WelcomePageSteps redirectLinkClick() {
        ui.welcomePage().REDIRECT_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Secure File Download'")
    public WelcomePageSteps secureFileDownloadClick() {
        ui.welcomePage().SECURE_FILE_DOWNLOAD_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Shadow DOM'")
    public WelcomePageSteps shadowDomClick() {
        ui.welcomePage().SHADOW_DOM_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Shifting Content'")
    public WelcomePageSteps shiftingContentClick() {
        ui.welcomePage().SHIFTING_CONTENT_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Sortable Data Tables'")
    public WelcomePageSteps sortableDataTablesClick() {
        ui.welcomePage().SORTABLE_DATA_TABLES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Status Codes'")
    public WelcomePageSteps statusCodesClick() {
        ui.welcomePage().STATUS_CODES_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'Typos'")
    public WelcomePageSteps typosClick() {
        ui.welcomePage().TYPOS_LINK.click();
        return this;
    }

    @Step("Нажать кнопку 'WYSIWYG Editor'")
    public WelcomePageSteps wysiwygEditorClick() {
        ui.welcomePage().WYSIWYG_EDITOR_LINK.click();
        return this;
    }
}
