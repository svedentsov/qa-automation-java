package com.svedentsov.ui.element;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.svedentsov.utils.DateUtil;
import org.openqa.selenium.By;
import com.svedentsov.ui.helper.Widget;

import java.time.LocalDate;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;

public class DatePicker extends Widget<DatePicker> {

    private final SelenideElement dataRange = Selenide.$("#widgetField");

    public DatePicker(By locator) {
        super(locator);
    }

    public List<LocalDate> getDataRange() {
        var range = dataRange.shouldBe(visible).getText();
        return DateUtil.getDateRange(range);
    }
}