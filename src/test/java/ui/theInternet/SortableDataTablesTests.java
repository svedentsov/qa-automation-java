package ui.theInternet;

import core.UITest;
import core.annotations.Layer;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@Layer("ui")
@Feature("Тестирование функциональности сортируемых таблиц")
@DisplayName("Тесты страницы 'Sortable Data Tables'")
public class SortableDataTablesTests extends UITest {

    @BeforeEach
    public void setUp() {
        open("https://the-internet.herokuapp.com");
        theInternet.welcomePageSteps()
                .sortableDataTablesClick();
    }

    @Test
    @Story("Проверка наличия записи в таблице")
    @DisplayName("Проверка наличия записи по фамилии и имени")
    public void checkRecordPresenceInTable() {
        // Act, Assert
        theInternet.sortableDataTablesSteps()
                .checkRecordIsPresentUsersTable("Last Name", "Smith")
                .checkRecordIsPresentUsersTable("First Name", "John");
    }

    @Test
    @Story("Проверка отсутствия записи в таблице")
    @DisplayName("Проверка отсутствия записи по фамилии и имени")
    public void checkRecordAbsenceInTable() {
        // Act, Assert
        theInternet.sortableDataTablesSteps()
                .checkRecordIsNotPresentUsersTable("Last Name", "Nonexistent")
                .checkRecordIsNotPresentUsersTable("First Name", "Unknown");
    }

    @Test
    @Story("Проверка информации в таблице")
    @DisplayName("Проверка информации по фамилии и имени")
    public void checkInfoInRecord() {
        // Act, Assert
        theInternet.sortableDataTablesSteps()
                .checkInfoFromFieldUsersTable("Last Name", "Smith", "Email", "jsmith@gmail.com")
                .checkInfoFromFieldUsersTable("First Name", "John", "Due", "$50.00");
    }
}
