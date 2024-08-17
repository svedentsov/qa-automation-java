package ui.widgets;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.DateUtil;
import ui.helper.Widget;
import org.openqa.selenium.By;

import java.time.LocalDate;
import java.util.List;

import static com.codeborne.selenide.Condition.*;

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