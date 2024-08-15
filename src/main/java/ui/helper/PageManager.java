package ui.helper;

import com.codeborne.selenide.Selenide;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ui.pages.*;

import java.util.Optional;

/**
 * Базовый роутер для навигации между страницами веб-приложения.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageManager {

    private static PageManager PageManager;

    public static synchronized PageManager getPageManager() {
        return Optional.ofNullable(PageManager).orElseGet(() -> PageManager = new PageManager());
    }

    /**
     * Возвращает страницу "A/B Testing".
     *
     * @return экземпляр {@link AbTestPage}
     */
    public AbTestPage abTestPage() {
        return Selenide.page(AbTestPage.class);
    }

    /**
     * Возвращает страницу "Add/Remove Elements".
     *
     * @return экземпляр {@link AddRemoveElementsPage}
     */
    public AddRemoveElementsPage addRemoveElementsPage() {
        return Selenide.page(AddRemoveElementsPage.class);
    }

    /**
     * Возвращает страницу "Basic Auth".
     *
     * @return экземпляр {@link BasicAuthPage}
     */
    public BasicAuthPage basicAuthPage() {
        return Selenide.page(BasicAuthPage.class);
    }

    /**
     * Возвращает страницу "Broken Images".
     *
     * @return экземпляр {@link BrokenImagesPage}
     */
    public BrokenImagesPage brokenImagesPage() {
        return Selenide.page(BrokenImagesPage.class);
    }

    /**
     * Возвращает страницу "Challenging DOM".
     *
     * @return экземпляр {@link ChallengingDomPage}
     */
    public ChallengingDomPage challengingDomPage() {
        return Selenide.page(ChallengingDomPage.class);
    }

    /**
     * Возвращает страницу "Checkboxes".
     *
     * @return экземпляр {@link CheckboxesPage}
     */
    public CheckboxesPage checkboxesPage() {
        return Selenide.page(CheckboxesPage.class);
    }

    /**
     * Возвращает страницу "Context Menu".
     *
     * @return экземпляр {@link ContextMenuPage}
     */
    public ContextMenuPage contextMenuPage() {
        return Selenide.page(ContextMenuPage.class);
    }

    /**
     * Возвращает страницу "Digest Authentication".
     *
     * @return экземпляр {@link DigestAuthPage}
     */
    public DigestAuthPage digestAuthPage() {
        return Selenide.page(DigestAuthPage.class);
    }

    /**
     * Возвращает страницу "Drag and Drop".
     *
     * @return экземпляр {@link DragAndDropPage}
     */
    public DragAndDropPage dragAndDropPage() {
        return Selenide.page(DragAndDropPage.class);
    }

    /**
     * Возвращает страницу "Dropdown".
     *
     * @return экземпляр {@link DropdownPage}
     */
    public DropdownPage dropdownPage() {
        return Selenide.page(DropdownPage.class);
    }

    /**
     * Возвращает страницу "Dynamic Content".
     *
     * @return экземпляр {@link DynamicContentPage}
     */
    public DynamicContentPage dynamicContentPage() {
        return Selenide.page(DynamicContentPage.class);
    }

    /**
     * Возвращает страницу "Dynamic Controls".
     *
     * @return экземпляр {@link DynamicControlsPage}
     */
    public DynamicControlsPage dynamicControlsPage() {
        return Selenide.page(DynamicControlsPage.class);
    }

    /**
     * Возвращает страницу "Dynamic Loading".
     *
     * @return экземпляр {@link DynamicLoadingPage}
     */
    public DynamicLoadingPage dynamicLoadingPage() {
        return Selenide.page(DynamicLoadingPage.class);
    }

    /**
     * Возвращает страницу "Entry Ad".
     *
     * @return экземпляр {@link EntryAdPage}
     */
    public EntryAdPage entryAdPage() {
        return Selenide.page(EntryAdPage.class);
    }

    /**
     * Возвращает страницу "Exit Intent".
     *
     * @return экземпляр {@link ExitIntentPage}
     */
    public ExitIntentPage exitIntentPage() {
        return Selenide.page(ExitIntentPage.class);
    }

    /**
     * Возвращает страницу "File Download".
     *
     * @return экземпляр {@link FileDownloadPage}
     */
    public FileDownloadPage fileDownloadPage() {
        return Selenide.page(FileDownloadPage.class);
    }

    /**
     * Возвращает страницу "File Upload".
     *
     * @return экземпляр {@link FileUploadPage}
     */
    public FileUploadPage fileUploadPage() {
        return Selenide.page(FileUploadPage.class);
    }

    /**
     * Возвращает страницу "Floating Menu".
     *
     * @return экземпляр {@link FloatingMenuPage}
     */
    public FloatingMenuPage floatingMenuPage() {
        return Selenide.page(FloatingMenuPage.class);
    }

    /**
     * Возвращает страницу "Forgot Password".
     *
     * @return экземпляр {@link ForgotPasswordPage}
     */
    public ForgotPasswordPage forgotPasswordPage() {
        return Selenide.page(ForgotPasswordPage.class);
    }

    /**
     * Возвращает страницу "Form Authentication".
     *
     * @return экземпляр {@link FormAuthenticationPage}
     */
    public FormAuthenticationPage formAuthenticationPage() {
        return Selenide.page(FormAuthenticationPage.class);
    }

    /**
     * Возвращает страницу "Frames".
     *
     * @return экземпляр {@link FramesPage}
     */
    public FramesPage framesPage() {
        return Selenide.page(FramesPage.class);
    }

    /**
     * Возвращает страницу "Geolocation".
     *
     * @return экземпляр {@link GeolocationPage}
     */
    public GeolocationPage geolocationPage() {
        return Selenide.page(GeolocationPage.class);
    }

    /**
     * Возвращает страницу "Horizontal Slider".
     *
     * @return экземпляр {@link HorizontalSliderPage}
     */
    public HorizontalSliderPage horizontalSliderPage() {
        return Selenide.page(HorizontalSliderPage.class);
    }

    /**
     * Возвращает страницу "Hovers".
     *
     * @return экземпляр {@link HoversPage}
     */
    public HoversPage hoversPage() {
        return Selenide.page(HoversPage.class);
    }

    /**
     * Возвращает страницу "Infinite Scroll".
     *
     * @return экземпляр {@link InfiniteScrollPage}
     */
    public InfiniteScrollPage infiniteScrollPage() {
        return Selenide.page(InfiniteScrollPage.class);
    }

    /**
     * Возвращает страницу "JavaScript Alerts".
     *
     * @return экземпляр {@link JavaScriptAlertsPage}
     */
    public JavaScriptAlertsPage javascriptAlertsPage() {
        return Selenide.page(JavaScriptAlertsPage.class);
    }

    /**
     * Возвращает страницу "JavaScript Error".
     *
     * @return экземпляр {@link JavaScriptErrorPage}
     */
    public JavaScriptErrorPage javascriptErrorPage() {
        return Selenide.page(JavaScriptErrorPage.class);
    }

    /**
     * Возвращает страницу "Key Presses".
     *
     * @return экземпляр {@link KeyPressesPage}
     */
    public KeyPressesPage keyPressesPage() {
        return Selenide.page(KeyPressesPage.class);
    }

    /**
     * Возвращает страницу "Large & Deep DOM".
     *
     * @return экземпляр {@link LargeDeepDomPage}
     */
    public LargeDeepDomPage largeDeepDomPage() {
        return Selenide.page(LargeDeepDomPage.class);
    }

    /**
     * Возвращает страницу "Multiple Windows".
     *
     * @return экземпляр {@link MultipleWindowsPage}
     */
    public MultipleWindowsPage multipleWindowsPage() {
        return Selenide.page(MultipleWindowsPage.class);
    }

    /**
     * Возвращает страницу "Nested Frames".
     *
     * @return экземпляр {@link NestedFramesPage}
     */
    public NestedFramesPage nestedFramesPage() {
        return Selenide.page(NestedFramesPage.class);
    }

    /**
     * Возвращает страницу "Notification Messages".
     *
     * @return экземпляр {@link NotificationMessagesPage}
     */
    public NotificationMessagesPage notificationMessagesPage() {
        return Selenide.page(NotificationMessagesPage.class);
    }

    /**
     * Возвращает страницу "Redirect Link".
     *
     * @return экземпляр {@link RedirectLinkPage}
     */
    public RedirectLinkPage redirectLinkPage() {
        return Selenide.page(RedirectLinkPage.class);
    }

    /**
     * Возвращает страницу "Secure File Download".
     *
     * @return экземпляр {@link SecureFileDownloadPage}
     */
    public SecureFileDownloadPage secureFileDownloadPage() {
        return Selenide.page(SecureFileDownloadPage.class);
    }

    /**
     * Возвращает страницу "Shadow DOM".
     *
     * @return экземпляр {@link ShadowDomPage}
     */
    public ShadowDomPage shadowDomPage() {
        return Selenide.page(ShadowDomPage.class);
    }

    /**
     * Возвращает страницу "Shifting Content".
     *
     * @return экземпляр {@link ShiftingContentPage}
     */
    public ShiftingContentPage shiftingContentPage() {
        return Selenide.page(ShiftingContentPage.class);
    }

    /**
     * Возвращает страницу "Sortable Data Tables".
     *
     * @return экземпляр {@link SortableDataTablesPage}
     */
    public SortableDataTablesPage sortableDataTablesPage() {
        return Selenide.page(SortableDataTablesPage.class);
    }

    /**
     * Возвращает страницу "Status Codes".
     *
     * @return экземпляр {@link StatusCodesPage}
     */
    public StatusCodesPage statusCodesPage() {
        return Selenide.page(StatusCodesPage.class);
    }

    /**
     * Возвращает страницу "Typos".
     *
     * @return экземпляр {@link TyposPage}
     */
    public TyposPage typosPage() {
        return Selenide.page(TyposPage.class);
    }

    /**
     * Возвращает страницу приветствия.
     *
     * @return экземпляр {@link WelcomePage}
     */
    public WelcomePage welcomePage() {
        return Selenide.page(WelcomePage.class);
    }

    /**
     * Возвращает страницу "WYSIWYG Editor".
     *
     * @return экземпляр {@link WysiwygEditorPage}
     */
    public WysiwygEditorPage wysiwygEditorPage() {
        return Selenide.page(WysiwygEditorPage.class);
    }
}
