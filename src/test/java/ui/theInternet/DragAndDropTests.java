package ui.theInternet;

import common.UITest;
import core.annotations.Layer;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@Layer("UI")
@Feature("Тестирование функции Drag and Drop")
@DisplayName("Тесты страницы 'Drag and Drop'")
public class DragAndDropTests extends UITest {

    @BeforeEach
    public void setUp() {
        open("https://the-internet.herokuapp.com");
        theInternet.welcomePageSteps()
                .dragAndDropClick();
    }

    @Test
    @Story("Перемещение контейнера A в контейнер B")
    @DisplayName("Перемещение контейнера A в контейнер B с проверкой обновленного текста")
    public void dragContainerAtoBAndVerifyText() {
        // Arrange
        theInternet.dragAndDropSteps()
                .verifyInitialState("A", "B");

        // Act
        theInternet.dragAndDropSteps()
                .dragAtoB();

        // Assert
        theInternet.dragAndDropSteps()
                .verifyContainerAText("B")
                .verifyContainerBText("A");
    }

    @Test
    @Story("Перемещение контейнера B в контейнер A")
    @DisplayName("Перемещение контейнера B в контейнер A с проверкой обновленного текста")
    public void dragContainerBtoAAndVerifyText() {
        // Arrange
        theInternet.dragAndDropSteps()
                .verifyInitialState("A", "B");

        // Act
        theInternet.dragAndDropSteps()
                .dragBtoA();

        // Assert
        theInternet.dragAndDropSteps()
                .verifyTextAfterDragAndDrop("B", "A");
    }
}
