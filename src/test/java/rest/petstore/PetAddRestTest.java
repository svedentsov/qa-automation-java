package rest.petstore;

import com.svedentsov.app.petstore.model.Pet;
import com.svedentsov.core.annotations.*;
import com.svedentsov.core.annotations.Epic;
import com.svedentsov.core.annotations.Feature;
import com.svedentsov.core.annotations.Story;
import core.RestTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import com.svedentsov.app.petstore.data.DataGenerator;

import static io.qameta.allure.SeverityLevel.*;
import static com.svedentsov.app.petstore.enums.PetStatus.AVAILABLE;

@Layer("rest")
@Owner("svedentsov")
@Epic("Регресс магазина питомцев")
@Feature("Питомец")
public class PetAddRestTest extends RestTest {

    private final String incorrectJson = DataGenerator.INCORRECT_JSON;
    private final long notFoundId = -1;
    private final Pet minDataPet = DataGenerator.generateMinDataPet();
    private final Pet fullDataPet = DataGenerator.generateFullDataPet();
    private final Pet modifiedPet = fullDataPet.toBuilder().name("SayMyName").build();

    @Test
    @Severity(BLOCKER)
    @TM4J("AE-T1")
    @Microservice("Repository")
    @JiraIssues({@JiraIssue("AE-2")})
    @Tags({@Tag("rest"), @Tag("regress")})
    @Story("Добавление питомца")
    @DisplayName("Добавление питомца в магазин с минимумом полей")
    @Description("Проверяет успешное добавление нового питомца в магазин c минимальным набором полей")
    public void createPetWithMinDataPetTest() {
        // Act, Assert
        petStore.petSteps()
                .createPetSuccessfully(minDataPet);
    }

    @Test
    @Severity(CRITICAL)
    @DisplayName("Добавление питомца в магазин со всеми полями")
    @Description("Проверяет успешное добавление нового питомца в магазин c максимальным набором полей")
    public void createPetWithFullDataPetTest() {
        // Act, Assert
        petStore.petSteps()
                .createPetSuccessfully(fullDataPet);
    }

    @Test
    @Severity(NORMAL)
    @DisplayName("Добавление питомца в магазин с некорректном телом запроса")
    @Description("Проверяет обработку некорректного тела запроса при добавлении питомца")
    public void createPetWithIncorrectJsonTest() {
        // Act, Assert
        petStore.petSteps()
                .postBadRequest(incorrectJson);
    }

    @Test
    @Severity(BLOCKER)
    @DisplayName("Удаление питомца из магазина")
    @Description("Проверяет успешное удаление питомца из магазина")
    public void deletePetTest() {
        // Arrange
        Pet createdPet = petStore.petSteps()
                .createPetSuccessfully(fullDataPet);
        // Act
        petStore.petSteps().
                deletePetById(createdPet.id());
        // Assert
        petStore.petSteps()
                .getNotFoundPetById(createdPet.id());
    }

    @Test
    @Severity(NORMAL)
    @DisplayName("Удаление питомца по несуществующему ID")
    @Description("Проверяет удаление из магазина несуществующему питомца по ID")
    public void deleteNotFoundPetTest() {
        // Act, Assert
        petStore.petSteps()
                .deleteNotFoundPetById(notFoundId);
    }

    @Test
    @Severity(BLOCKER)
    @DisplayName("Получение питомца по ID существующего питомца")
    @Description("Проверяет получение правильного питомца по указанному ID")
    public void getPetByIdTest() {
        // Arrange
        Pet createdPet = petStore.petSteps()
                .createPetSuccessfully(fullDataPet);
        // Act, Assert
        petStore.petSteps()
                .assertPetData(createdPet);
    }

    @Test
    @Severity(NORMAL)
    @DisplayName("Получение питомца по ID несуществующего питомца")
    @Description("Проверяет получение из магазина несуществующего питомца по ID")
    public void getNotFoundPetTest() {
        // Act, Assert
        petStore.petSteps()
                .getNotFoundPetById(notFoundId);
    }

    @Test
    @Severity(CRITICAL)
    @DisplayName("Обновление существующего питомца методом PUT")
    @Description("Проверяет корректное обновление существующего питомца")
    public void updateFullDataPetTest() {
        // Arrange
        petStore.petSteps()
                .createPetSuccessfully(fullDataPet);
        // Act
        petStore.petSteps()
                .putPetSuccessfully(modifiedPet)
                .assertPetData(modifiedPet);
    }

    @Test
    @Severity(NORMAL)
    @DisplayName("Добавление нового питомца методом PUT")
    @Description("Проверяет создание нового питомца методом PUT")
    public void putNewPetTest() {
        // Act, Assert
        petStore.petSteps()
                .putPetSuccessfully(fullDataPet)
                .assertPetData(fullDataPet);
    }

    @Test
    @Severity(NORMAL)
    @DisplayName("Обработка некорректного тела запроса методом PUT")
    @Description("Проверяет обработку некорректного тела запроса методом PUT")
    public void putIncorrectJsonTest() {
        // Act, Assert
        petStore.petSteps()
                .putBadRequest(incorrectJson);
    }

    @Test
    @Severity(BLOCKER)
    @DisplayName("Поиск питомцев по статусу")
    @Description("Проверяет корректный поиск питомцев по их статусу")
    public void findPetsByStatusTest() {
        // Act, Assert
        petStore.petSteps()
                .findPetsByStatus(AVAILABLE);
    }

    @Test
    @Severity(BLOCKER)
    @DisplayName("Обновление питомца с использованием формы")
    @Description("Проверяет корректное обновление питомца с использованием формы")
    public void updatePetWithFormTest() {
        // Arrange
        petStore.petSteps()
                .createPetSuccessfully(fullDataPet);
        // Act
        petStore.petSteps()
                .updatePetWithForm(modifiedPet)
                .assertPetData(modifiedPet);
    }
}
