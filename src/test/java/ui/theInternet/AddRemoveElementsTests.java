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
@Feature("Тестирование функциональности добавления и удаления элементов")
@DisplayName("Тесты страницы 'Add/Remove Elements'")
public class AddRemoveElementsTests extends UITest {

    @BeforeEach
    public void setUp() {
        open("https://the-internet.herokuapp.com");
        theInternet.welcomePageSteps()
                .addRemoveElementsClick();
    }

    @Test
    @Story("Проверка текста сообщения")
    @DisplayName("Проверка отображения и содержания текста сообщения")
    public void messageTextIsDisplayedAndCorrect() {
        // Arrange
        theInternet.addRemoveElementsSteps()
                .addDeleteButton();

        // Act & Assert
        theInternet.addRemoveElementsSteps()
                .checkTitleTextIsVisible()
                .checkTitleText("Add/Remove Elements");
    }

    @Test
    @Story("Добавление кнопки 'Удалить'")
    @DisplayName("Добавление одной кнопки 'Удалить'")
    public void addSingleDeleteButton() {
        // Act
        theInternet.addRemoveElementsSteps()
                .addDeleteButton();

        // Assert
        theInternet.addRemoveElementsSteps()
                .checkDeleteButtonIsExist()
                .checkNumberOfDeleteButtons(1);
    }

    @Test
    @Story("Удаление кнопки 'Удалить'")
    @DisplayName("Удаление одной кнопки 'Удалить'")
    public void removeSingleDeleteButton() {
        // Arrange
        theInternet.addRemoveElementsSteps()
                .addDeleteButton();

        // Act
        theInternet.addRemoveElementsSteps()
                .clickDeleteButton();

        // Assert
        theInternet.addRemoveElementsSteps()
                .checkDeleteButtonIsNotExist()
                .checkNumberOfDeleteButtons(0);
    }

    @Test
    @Story("Добавление нескольких кнопок 'Удалить'")
    @DisplayName("Добавление нескольких кнопок 'Удалить'")
    public void addMultipleDeleteButtons() {
        // Act
        theInternet.addRemoveElementsSteps()
                .addDeleteButtons(3);

        // Assert
        theInternet.addRemoveElementsSteps()
                .checkNumberOfDeleteButtons(3);
    }

    @Test
    @Story("Удаление всех кнопок 'Удалить'")
    @DisplayName("Удаление всех добавленных кнопок 'Удалить'")
    public void removeAllDeleteButtons() {
        // Arrange
        theInternet.addRemoveElementsSteps()
                .addDeleteButtons(3);

        // Act
        theInternet.addRemoveElementsSteps()
                .removeDeleteButtons(3);

        // Assert
        theInternet.addRemoveElementsSteps()
                .checkNumberOfDeleteButtons(0);
    }

    @Test
    @Story("Частичное удаление кнопок 'Удалить'")
    @DisplayName("Удаление нескольких из добавленных кнопок 'Удалить'")
    public void removeSomeOfMultipleDeleteButtons() {
        // Arrange
        theInternet.addRemoveElementsSteps()
                .addDeleteButtons(5);

        // Act
        theInternet.addRemoveElementsSteps()
                .removeDeleteButtons(2);

        // Assert
        theInternet.addRemoveElementsSteps()
                .checkNumberOfDeleteButtons(3);
    }
}
