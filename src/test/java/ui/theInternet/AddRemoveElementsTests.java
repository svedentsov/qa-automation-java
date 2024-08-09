package ui.theInternet;

import common.BaseTest;
import core.annotation.Layer;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@Layer("UI")
@Feature("Add/Remove Elements Tests")
@DisplayName("Тесты для страницы 'Add/Remove Elements'")
public class AddRemoveElementsTests extends BaseTest {

    @BeforeEach
    public void setUp() {
        open("https://the-internet.herokuapp.com");
        theInternet.welcomePageSteps().addRemoveElementsClick();
    }

    @Test
    @Story("Проверка текста сообщения")
    @DisplayName("Проверка отображения и содержания текста сообщения")
    public void testMessageTextIsDisplayedAndCorrect() {
        // Arrange
        theInternet.addRemoveElementsSteps()
                .clickAddButton();

        // Act & Assert
        theInternet.addRemoveElementsSteps()
                .verifyTitleTextIsVisible()
                .verifyTitleText("Add/Remove Elements");
    }

    @Test
    @Story("Добавление кнопки 'Удалить'")
    @DisplayName("Добавление одной кнопки 'Удалить'")
    public void testAddSingleDeleteButton() {
        // Act
        theInternet.addRemoveElementsSteps()
                .clickAddButton();

        // Assert
        theInternet.addRemoveElementsSteps()
                .checkDeleteButtonIsExist()
                .verifyNumberOfDeleteButtons(1);
    }

    @Test
    @Story("Удаление кнопки 'Удалить'")
    @DisplayName("Удаление одной кнопки 'Удалить'")
    public void testRemoveSingleDeleteButton() {
        // Arrange
        theInternet.addRemoveElementsSteps()
                .clickAddButton();

        // Act
        theInternet.addRemoveElementsSteps()
                .clickDeleteButton();

        // Assert
        theInternet.addRemoveElementsSteps()
                .checkDeleteButtonIsNotExist()
                .verifyNumberOfDeleteButtons(0);
    }

    @Test
    @Story("Добавление нескольких кнопок 'Удалить'")
    @DisplayName("Добавление нескольких кнопок 'Удалить'")
    public void testAddMultipleDeleteButtons() {
        // Act
        theInternet.addRemoveElementsSteps()
                .addDeleteButtons(3);

        // Assert
        theInternet.addRemoveElementsSteps()
                .verifyNumberOfDeleteButtons(3);
    }

    @Test
    @Story("Удаление всех кнопок 'Удалить'")
    @DisplayName("Удаление всех добавленных кнопок 'Удалить'")
    public void testRemoveAllDeleteButtons() {
        // Arrange
        theInternet.addRemoveElementsSteps()
                .addDeleteButtons(3);

        // Act
        theInternet.addRemoveElementsSteps()
                .removeDeleteButtons(3);

        // Assert
        theInternet.addRemoveElementsSteps()
                .verifyNumberOfDeleteButtons(0);
    }

    @Test
    @Story("Частичное удаление кнопок 'Удалить'")
    @DisplayName("Удаление нескольких из добавленных кнопок 'Удалить'")
    public void testRemoveSomeOfMultipleDeleteButtons() {
        // Arrange
        theInternet.addRemoveElementsSteps()
                .addDeleteButtons(5);

        // Act
        theInternet.addRemoveElementsSteps()
                .removeDeleteButtons(2);

        // Assert
        theInternet.addRemoveElementsSteps()
                .verifyNumberOfDeleteButtons(3);
    }
}
