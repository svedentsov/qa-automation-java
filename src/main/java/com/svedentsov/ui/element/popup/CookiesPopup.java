package com.svedentsov.ui.element.popup;

import com.svedentsov.ui.helper.Widget;
import org.openqa.selenium.By;

public class CookiesPopup extends Widget<CookiesPopup> {

    private final By modifyButton = By.cssSelector(".uk-cookie-close");
    private final By acceptButton = By.cssSelector(".uk-cookie-body");

    public CookiesPopup(By locator) {
        super(locator);
    }
}
