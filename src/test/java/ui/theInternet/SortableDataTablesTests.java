package ui.theInternet;

import common.BaseTest;
import common.UITest;
import core.annotations.Layer;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@Layer("UI")
@Feature("Тестирование таблиц данных")
@DisplayName("Тесты страницы 'Sortable Data Tables'")
public class SortableDataTablesTests extends UITest {

    @BeforeEach
    public void setUp() {
        open("https://the-internet.herokuapp.com");
        theInternet.welcomePageSteps()
                .sortableDataTablesClick();
    }

    @Test
    @Story("Проверка наличия записи")
    @DisplayName("Проверка наличия записи в таблице данных")
    public void checkRecordPresenceInTable() {
        // Act, Assert
        theInternet.sortableDataTablesSteps()
                .checkRecordIsPresentUsersTable("Last Name", "Smith")
                .checkRecordIsPresentUsersTable("First Name", "John");
    }

    @Test
    @Story("Проверка отсутствия записи")
    @DisplayName("Проверка отсутствия записи в таблице данных")
    public void checkRecordAbsenceInTable() {
        // Act, Assert
        theInternet.sortableDataTablesSteps()
                .checkRecordIsNotPresentUsersTable("Last Name", "Nonexistent")
                .checkRecordIsNotPresentUsersTable("First Name", "Unknown");
    }

    @Test
    @Story("Проверка данных записи")
    @DisplayName("Проверка правильности данных в записи таблицы")
    public void checkInfoInRecord() {
        // Act, Assert
        theInternet.sortableDataTablesSteps()
                .checkInfoFromFieldUsersTable("Last Name", "Smith", "Email", "jsmith@gmail.com")
                .checkInfoFromFieldUsersTable("First Name", "John", "Due", "$50.00");
    }
}
