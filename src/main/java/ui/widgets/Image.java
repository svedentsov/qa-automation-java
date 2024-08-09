package ui.widgets;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;
import ui.pages.UIRouter;

import static com.codeborne.selenide.Selenide.$;

public class Image extends UIRouter {

    private final By locator;

    public Image(By selector) {
        this.locator = selector;
    }

    public UIRouter click() {
        $(locator).shouldBe(Condition.enabled).click();
        return this;
    }
}
