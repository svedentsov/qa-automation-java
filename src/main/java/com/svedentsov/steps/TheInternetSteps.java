package com.svedentsov.steps;

import com.svedentsov.steps.theinternet.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.svedentsov.steps.manager.UiManager;

import java.util.Optional;

/**
 * Класс для управления шагами на страницах приложения "The Internet".
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TheInternetSteps {

    private static TheInternetSteps theInternetSteps;
    private UiManager uiManager;
    private AbTestSteps abTestSteps;
    private AddRemoveElementsSteps addRemoveElementsSteps;
    private BasicAuthSteps basicAuthSteps;
    private BrokenImagesSteps brokenImagesSteps;
    private ChallengingDomSteps challengingDomSteps;
    private CheckboxesSteps checkboxesSteps;
    private ContextMenuSteps contextMenuSteps;
    private DigestAuthSteps digestAuthSteps;
    private DragAndDropSteps dragAndDropSteps;
    private DropdownSteps dropdownSteps;
    private DynamicContentSteps dynamicContentSteps;
    private DynamicControlsSteps dynamicControlsSteps;
    private DynamicLoadingSteps dynamicLoadingSteps;
    private EntryAdSteps entryAdSteps;
    private ExitIntentSteps exitIntentSteps;
    private FileDownloadSteps fileDownloadSteps;
    private FileUploadSteps fileUploadSteps;
    private FloatingMenuSteps floatingMenuSteps;
    private ForgotPasswordSteps forgotPasswordSteps;
    private FormAuthenticationSteps formAuthenticationSteps;
    private FramesSteps framesSteps;
    private GeolocationSteps geolocationSteps;
    private HorizontalSliderSteps horizontalSliderSteps;
    private HoversSteps hoversSteps;
    private InfiniteScrollSteps infiniteScrollSteps;
    private JavaScriptAlertsSteps javascriptAlertsSteps;
    private JavaScriptErrorSteps javascriptErrorSteps;
    private KeyPressesSteps keyPressesSteps;
    private LargeDeepDomSteps largeDeepDomSteps;
    private MultipleWindowsSteps multipleWindowsSteps;
    private NestedFramesSteps nestedFramesSteps;
    private NotificationMessagesSteps notificationMessagesSteps;
    private RedirectLinkSteps redirectLinkSteps;
    private SecureFileDownloadSteps secureFileDownloadSteps;
    private SortableDataTablesSteps sortableDataTablesSteps;
    private ShadowDomSteps shadowDomSteps;
    private ShiftingContentSteps shiftingContentSteps;
    private StatusCodesSteps statusCodesSteps;
    private TyposSteps typosSteps;
    private WelcomePageSteps welcomePageSteps;
    private WysiwygEditorSteps wysiwygEditorSteps;

    /**
     * Возвращает единственный экземпляр AppTheInternet.
     *
     * @return экземпляр {@link TheInternetSteps}
     */
    public synchronized static TheInternetSteps getTheInternet() {
        return Optional.ofNullable(theInternetSteps).orElseGet(() -> theInternetSteps = new TheInternetSteps());
    }

    public UiManager ui() {
        return Optional.ofNullable(uiManager).orElseGet(() -> uiManager = UiManager.getManager());
    }

    /**
     * Возвращает шаги для страницы "A/B Testing".
     *
     * @return шаги для {@link AbTestSteps}
     */
    public AbTestSteps abTestSteps() {
        return Optional.ofNullable(abTestSteps).orElseGet(() -> abTestSteps = new AbTestSteps());
    }

    /**
     * Возвращает шаги для страницы "Add/Remove Elements".
     *
     * @return шаги для {@link AddRemoveElementsSteps}
     */
    public AddRemoveElementsSteps addRemoveElementsSteps() {
        return Optional.ofNullable(addRemoveElementsSteps).orElseGet(() -> addRemoveElementsSteps = new AddRemoveElementsSteps());
    }

    /**
     * Возвращает шаги для страницы "Basic Auth".
     *
     * @return шаги для {@link BasicAuthSteps}
     */
    public BasicAuthSteps basicAuthSteps() {
        return Optional.ofNullable(basicAuthSteps).orElseGet(() -> basicAuthSteps = new BasicAuthSteps());
    }

    /**
     * Возвращает шаги для страницы "Broken Images".
     *
     * @return шаги для {@link BrokenImagesSteps}
     */
    public BrokenImagesSteps brokenImagesSteps() {
        return Optional.ofNullable(brokenImagesSteps).orElseGet(() -> brokenImagesSteps = new BrokenImagesSteps());
    }

    public ChallengingDomSteps challengingDomSteps() {
        return Optional.ofNullable(challengingDomSteps).orElseGet(() -> challengingDomSteps = new ChallengingDomSteps());
    }

    /**
     * Возвращает шаги для страницы "Checkboxes".
     *
     * @return шаги для {@link CheckboxesSteps}
     */
    public CheckboxesSteps checkboxesSteps() {
        return Optional.ofNullable(checkboxesSteps).orElseGet(() -> checkboxesSteps = new CheckboxesSteps());
    }

    /**
     * Возвращает шаги для страницы "Context Menu".
     *
     * @return шаги для {@link ContextMenuSteps}
     */
    public ContextMenuSteps contextMenuSteps() {
        return Optional.ofNullable(contextMenuSteps).orElseGet(() -> contextMenuSteps = new ContextMenuSteps());
    }

    /**
     * Возвращает шаги для страницы "Digest Authentication".
     *
     * @return шаги для {@link DigestAuthSteps}
     */
    public DigestAuthSteps digestAuthSteps() {
        return Optional.ofNullable(digestAuthSteps).orElseGet(() -> digestAuthSteps = new DigestAuthSteps());
    }

    /**
     * Возвращает шаги для страницы "Drag and Drop".
     *
     * @return шаги для {@link DragAndDropSteps}
     */
    public DragAndDropSteps dragAndDropSteps() {
        return Optional.ofNullable(dragAndDropSteps).orElseGet(() -> dragAndDropSteps = new DragAndDropSteps());
    }

    /**
     * Возвращает шаги для страницы "Dropdown".
     *
     * @return шаги для {@link DropdownSteps}
     */
    public DropdownSteps dropdownSteps() {
        return Optional.ofNullable(dropdownSteps).orElseGet(() -> dropdownSteps = new DropdownSteps());
    }

    /**
     * Возвращает шаги для страницы "Dynamic Content".
     *
     * @return шаги для {@link DynamicContentSteps}
     */
    public DynamicContentSteps dynamicContentSteps() {
        return Optional.ofNullable(dynamicContentSteps).orElseGet(() -> dynamicContentSteps = new DynamicContentSteps());
    }

    /**
     * Возвращает шаги для страницы "Dynamic Controls".
     *
     * @return шаги для {@link DynamicControlsSteps}
     */
    public DynamicControlsSteps dynamicControlsSteps() {
        return Optional.ofNullable(dynamicControlsSteps).orElseGet(() -> dynamicControlsSteps = new DynamicControlsSteps());
    }

    /**
     * Возвращает шаги для страницы "Dynamic Loading".
     *
     * @return шаги для {@link DynamicLoadingSteps}
     */
    public DynamicLoadingSteps dynamicLoadingSteps() {
        return Optional.ofNullable(dynamicLoadingSteps).orElseGet(() -> dynamicLoadingSteps = new DynamicLoadingSteps());
    }

    /**
     * Возвращает шаги для страницы "Entry Ad".
     *
     * @return шаги для {@link EntryAdSteps}
     */
    public EntryAdSteps entryAdSteps() {
        return Optional.ofNullable(entryAdSteps).orElseGet(() -> entryAdSteps = new EntryAdSteps());
    }

    /**
     * Возвращает шаги для страницы "Exit Intent".
     *
     * @return шаги для {@link ExitIntentSteps}
     */
    public ExitIntentSteps exitIntentSteps() {
        return Optional.ofNullable(exitIntentSteps).orElseGet(() -> exitIntentSteps = new ExitIntentSteps());
    }

    /**
     * Возвращает шаги для страницы "File Download".
     *
     * @return шаги для {@link FileDownloadSteps}
     */
    public FileDownloadSteps fileDownloadSteps() {
        return Optional.ofNullable(fileDownloadSteps).orElseGet(() -> fileDownloadSteps = new FileDownloadSteps());
    }

    /**
     * Возвращает шаги для страницы "File Upload".
     *
     * @return шаги для {@link FileUploadSteps}
     */
    public FileUploadSteps fileUploadSteps() {
        return Optional.ofNullable(fileUploadSteps).orElseGet(() -> fileUploadSteps = new FileUploadSteps());
    }

    /**
     * Возвращает шаги для страницы "Floating Menu".
     *
     * @return шаги для {@link FloatingMenuSteps}
     */
    public FloatingMenuSteps floatingMenuSteps() {
        return Optional.ofNullable(floatingMenuSteps).orElseGet(() -> floatingMenuSteps = new FloatingMenuSteps());
    }

    /**
     * Возвращает шаги для страницы "Forgot Password".
     *
     * @return шаги для {@link ForgotPasswordSteps}
     */
    public ForgotPasswordSteps forgotPasswordSteps() {
        return Optional.ofNullable(forgotPasswordSteps).orElseGet(() -> forgotPasswordSteps = new ForgotPasswordSteps());
    }

    /**
     * Возвращает шаги для страницы "Form Authentication".
     *
     * @return шаги для {@link FormAuthenticationSteps}
     */
    public FormAuthenticationSteps formAuthenticationSteps() {
        return Optional.ofNullable(formAuthenticationSteps).orElseGet(() -> formAuthenticationSteps = new FormAuthenticationSteps());
    }

    /**
     * Возвращает шаги для страницы "Frames".
     *
     * @return шаги для {@link FramesSteps}
     */
    public FramesSteps framesSteps() {
        return Optional.ofNullable(framesSteps).orElseGet(() -> framesSteps = new FramesSteps());
    }

    /**
     * Возвращает шаги для страницы "Geolocation".
     *
     * @return шаги для {@link GeolocationSteps}
     */
    public GeolocationSteps geolocationSteps() {
        return Optional.ofNullable(geolocationSteps).orElseGet(() -> geolocationSteps = new GeolocationSteps());
    }

    /**
     * Возвращает шаги для страницы "Horizontal Slider".
     *
     * @return шаги для {@link HorizontalSliderSteps}
     */
    public HorizontalSliderSteps horizontalSliderSteps() {
        return Optional.ofNullable(horizontalSliderSteps).orElseGet(() -> horizontalSliderSteps = new HorizontalSliderSteps());
    }

    /**
     * Возвращает шаги для страницы "Hovers".
     *
     * @return шаги для {@link HoversSteps}
     */
    public HoversSteps hoversSteps() {
        return Optional.ofNullable(hoversSteps).orElseGet(() -> hoversSteps = new HoversSteps());
    }

    /**
     * Возвращает шаги для страницы "Infinite Scroll".
     *
     * @return шаги для {@link InfiniteScrollSteps}
     */
    public InfiniteScrollSteps infiniteScrollSteps() {
        return Optional.ofNullable(infiniteScrollSteps).orElseGet(() -> infiniteScrollSteps = new InfiniteScrollSteps());
    }

    /**
     * Возвращает шаги для страницы "JavaScript Alerts".
     *
     * @return шаги для {@link JavaScriptAlertsSteps}
     */
    public JavaScriptAlertsSteps javascriptAlertsSteps() {
        return Optional.ofNullable(javascriptAlertsSteps).orElseGet(() -> javascriptAlertsSteps = new JavaScriptAlertsSteps());
    }

    /**
     * Возвращает шаги для страницы "JavaScript Error".
     *
     * @return шаги для {@link JavaScriptErrorSteps}
     */
    public JavaScriptErrorSteps javascriptErrorSteps() {
        return Optional.ofNullable(javascriptErrorSteps).orElseGet(() -> javascriptErrorSteps = new JavaScriptErrorSteps());
    }

    /**
     * Возвращает шаги для страницы "Key Presses".
     *
     * @return шаги для {@link KeyPressesSteps}
     */
    public KeyPressesSteps keyPressesSteps() {
        return Optional.ofNullable(keyPressesSteps).orElseGet(() -> keyPressesSteps = new KeyPressesSteps());
    }

    /**
     * Возвращает шаги для страницы "Large & Deep DOM".
     *
     * @return шаги для {@link LargeDeepDomSteps}
     */
    public LargeDeepDomSteps largeDeepDomSteps() {
        return Optional.ofNullable(largeDeepDomSteps).orElseGet(() -> largeDeepDomSteps = new LargeDeepDomSteps());
    }

    /**
     * Возвращает шаги для страницы "Multiple Windows".
     *
     * @return шаги для {@link MultipleWindowsSteps}
     */
    public MultipleWindowsSteps multipleWindowsSteps() {
        return Optional.ofNullable(multipleWindowsSteps).orElseGet(() -> multipleWindowsSteps = new MultipleWindowsSteps());
    }

    /**
     * Возвращает шаги для страницы "Nested Frames".
     *
     * @return шаги для {@link NestedFramesSteps}
     */
    public NestedFramesSteps nestedFramesSteps() {
        return Optional.ofNullable(nestedFramesSteps).orElseGet(() -> nestedFramesSteps = new NestedFramesSteps());
    }

    /**
     * Возвращает шаги для страницы "Notification Messages".
     *
     * @return шаги для {@link NotificationMessagesSteps}
     */
    public NotificationMessagesSteps notificationMessagesSteps() {
        return Optional.ofNullable(notificationMessagesSteps).orElseGet(() -> notificationMessagesSteps = new NotificationMessagesSteps());
    }

    /**
     * Возвращает шаги для страницы "Redirect Link".
     *
     * @return шаги для {@link RedirectLinkSteps}
     */
    public RedirectLinkSteps redirectLinkSteps() {
        return Optional.ofNullable(redirectLinkSteps).orElseGet(() -> redirectLinkSteps = new RedirectLinkSteps());
    }

    /**
     * Возвращает шаги для страницы "Secure File Download".
     *
     * @return шаги для {@link SecureFileDownloadSteps}
     */
    public SecureFileDownloadSteps secureFileDownloadSteps() {
        return Optional.ofNullable(secureFileDownloadSteps).orElseGet(() -> secureFileDownloadSteps = new SecureFileDownloadSteps());
    }

    /**
     * Возвращает шаги для страницы "Shadow DOM".
     *
     * @return шаги для {@link ShadowDomSteps}
     */
    public ShadowDomSteps shadowDomSteps() {
        return Optional.ofNullable(shadowDomSteps).orElseGet(() -> shadowDomSteps = new ShadowDomSteps());
    }

    /**
     * Возвращает шаги для страницы "Shifting Content".
     *
     * @return шаги для {@link ShiftingContentSteps}
     */
    public ShiftingContentSteps shiftingContentSteps() {
        return Optional.ofNullable(shiftingContentSteps).orElseGet(() -> shiftingContentSteps = new ShiftingContentSteps());
    }

    /**
     * Возвращает шаги для страницы "Sortable Data Tables".
     *
     * @return шаги для {@link SortableDataTablesSteps}
     */
    public SortableDataTablesSteps sortableDataTablesSteps() {
        return Optional.ofNullable(sortableDataTablesSteps).orElseGet(() -> sortableDataTablesSteps = new SortableDataTablesSteps());
    }

    /**
     * Возвращает шаги для страницы "Status Codes".
     *
     * @return шаги для {@link StatusCodesSteps}
     */
    public StatusCodesSteps statusCodesSteps() {
        return Optional.ofNullable(statusCodesSteps).orElseGet(() -> statusCodesSteps = new StatusCodesSteps());
    }

    /**
     * Возвращает шаги для страницы "Typos".
     *
     * @return шаги для {@link TyposSteps}
     */
    public TyposSteps typosSteps() {
        return Optional.ofNullable(typosSteps).orElseGet(() -> typosSteps = new TyposSteps());
    }

    /**
     * Возвращает шаги для страницы приветствия.
     *
     * @return шаги для {@link WelcomePageSteps}
     */
    public WelcomePageSteps welcomePageSteps() {
        return Optional.ofNullable(welcomePageSteps).orElseGet(() -> welcomePageSteps = new WelcomePageSteps());
    }

    /**
     * Возвращает шаги для страницы "WYSIWYG Editor".
     *
     * @return шаги для {@link WysiwygEditorSteps}
     */
    public WysiwygEditorSteps wysiwygEditorSteps() {
        return Optional.ofNullable(wysiwygEditorSteps).orElseGet(() -> wysiwygEditorSteps = new WysiwygEditorSteps());
    }
}
