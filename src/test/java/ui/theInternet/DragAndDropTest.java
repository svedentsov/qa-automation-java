package ui.theInternet;

import common.UITest;
import core.annotations.Layer;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@Layer("ui")
@Feature("Тестирование функциональности перетаскивания")
@DisplayName("Тесты страницы 'Drag and Drop'")
public class DragAndDropTest extends UITest {

    @BeforeEach
    public void setUp() {
        open("https://the-internet.herokuapp.com");
        theInternet.welcomePageSteps()
                .dragAndDropClick();
    }

    @Test
    @Story("Перемещение контейнера A в контейнер B")
    @DisplayName("Проверка результата перетаскивания элемента A в контейнер B")
    public void dragContainerAtoBAndVerifyText() {
        // Arrange
        theInternet.dragAndDropSteps()
                .checkInitialState("A", "B");

        // Act
        theInternet.dragAndDropSteps()
                .dragAtoB();

        // Assert
        theInternet.dragAndDropSteps()
                .checkContainerAText("B")
                .checkContainerBText("A");
    }

    @Test
    @Story("Перемещение контейнера B в контейнер A")
    @DisplayName("Проверка результата перемещения элемента B в элемента A")
    public void dragContainerBtoAAndVerifyText() {
        // Arrange
        theInternet.dragAndDropSteps()
                .checkInitialState("A", "B");

        // Act
        theInternet.dragAndDropSteps()
                .dragBtoA();

        // Assert
        theInternet.dragAndDropSteps()
                .checkTextAfterDragAndDrop("B", "A");
    }
}
