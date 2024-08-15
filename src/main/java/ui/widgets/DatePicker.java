package ui.widgets;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.DateUtil;
import core.widgets.Widget;
import org.openqa.selenium.By;

import java.time.LocalDate;
import java.util.List;

public class DatePicker extends Widget<DatePicker> {

    private final SelenideElement dataRange = Selenide.$("#widgetField");

    public DatePicker(By locator) {
        super(locator);
    }

    public List<LocalDate> getDataRange() {
        var range = dataRange.shouldBe(Condition.visible).getText();
        return DateUtil.getDateRange(range);
    }
}