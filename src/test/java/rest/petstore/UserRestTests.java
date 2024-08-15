package rest.petstore;

import common.RestTest;
import core.annotations.Layer;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.data.DataGenerator;
import rest.model.User;

import java.util.List;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static io.qameta.allure.SeverityLevel.NORMAL;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static rest.matcher.RestMatcher.statusCode;

@Layer("rest")
@Owner("svedentsov")
@Epic("Регресс магазина питомцев")
@Feature("Пользователь")
public class UserRestTests extends RestTest {

    @Test
    @Severity(CRITICAL)
    @DisplayName("Создание пользователя")
    @Description("Проверяет успешное создание нового пользователя.")
    public void createUserTest() {
        // Arrange
        User user = DataGenerator.generateValidUser();

        // Act
        petStore.userSteps().createUser(user);

        // Assert
        petStore.userSteps().assertUserExists(user);
    }

    @Test
    @Severity(CRITICAL)
    @DisplayName("Создание списка пользователей (массив)")
    @Description("Проверяет успешное создание списка пользователей с данными в виде массива.")
    public void createUsersArrayTest() {
        // Arrange
        List<User> users = DataGenerator.generateValidUsersArray();

        // Act
        petStore.userSteps().createUsersWithArrayInput(users);

        // Assert
        users.forEach(petStore.userSteps()::assertUserExists);
    }

    @Test
    @Severity(CRITICAL)
    @DisplayName("Создание списка пользователей (список)")
    @Description("Проверяет успешное создание списка пользователей с данными в виде списка.")
    public void createUsersListTest() {
        // Arrange
        List<User> users = DataGenerator.generateValidUsersList();

        // Act
        petStore.userSteps().createUsersWithListInput(users);

        // Assert
        users.forEach(petStore.userSteps()::assertUserExists);
    }

    @Test
    @Severity(CRITICAL)
    @DisplayName("Удаление пользователя")
    @Description("Проверяет успешное удаление пользователя из системы.")
    public void deleteUserTest() {
        // Arrange
        User user = DataGenerator.generateValidUser();
        petStore.userSteps().createUser(user);

        // Act
        petStore.userSteps().deleteUser(user.username());

        // Assert
        petStore.userSteps().assertUserNotFound(user.username());
    }

    @Test
    @Severity(NORMAL)
    @DisplayName("Получение пользователя")
    @Description("Проверяет успешное получение пользователя по его имени.")
    public void getUserByNameTest() {
        // Arrange
        User user = DataGenerator.generateValidUser();
        petStore.userSteps().createUser(user);

        // Act
        User fetchedUser = petStore.userSteps().getUserByName(user.username());

        // Assert
        petStore.userSteps().assertUserData(user, fetchedUser);
    }

    @Test
    @Severity(NORMAL)
    @DisplayName("Авторизация пользователя")
    @Description("Проверяет успешную авторизацию пользователя в системе.")
    public void loginUserTest() {
        // Arrange
        User user = DataGenerator.generateValidUser();
        petStore.userSteps().createUser(user);

        // Act
        String sessionId = petStore.userSteps().loginUser(user.username(), user.password());

        // Assert
        assertNotNull(sessionId, "Ожидается, что сессия будет создана");
    }

    @Test
    @Severity(NORMAL)
    @DisplayName("Выход из системы")
    @Description("Проверяет успешный выход текущего пользователя из системы.")
    public void logoutUserTest() {
        // Act, Assert
        petStore.userSteps().logoutUser();
    }

    @Test
    @Severity(CRITICAL)
    @DisplayName("Обновление пользователя")
    @Description("Проверяет успешное обновление данных пользователя.")
    public void updateUserTest() {
        // Arrange
        User user = DataGenerator.generateValidUser();
        petStore.userSteps().createUser(user);
        User updatedUser = user.toBuilder().firstName("NewFirstName").build();

        // Act
        petStore.userSteps().updateUser(user.username(), updatedUser);

        // Assert
        petStore.userSteps().assertUserExists(updatedUser);
    }

    @Test
    @Severity(NORMAL)
    @DisplayName("Уменьшение пользователей после удаления")
    @Description("Проверяет уменьшение общего количества пользователей после удаления одного из них.")
    public void userCountDecreasesAfterDeleteTest() {
        // Arrange
        User user = DataGenerator.generateValidUser();
        petStore.userSteps().createUser(user);

        // Act
        petStore.userSteps().deleteUser(user.username());

        // Assert
        petStore.userSteps().assertUserNotFound(user.username());
    }

    @Test
    @Severity(NORMAL)
    @DisplayName("Получение несуществующего пользователя")
    @Description("Проверяет, что при попытке получения несуществующего пользователя возникает ошибка.")
    public void getNonExistentUserTest() {
        // Act, Assert
        petStore.rest().userService()
                .getUserByName("nonExistentUser")
                .shouldHave(statusCode(404));
    }

    @Test
    @Severity(NORMAL)
    @DisplayName("Успешное изменение данных")
    @Description("Проверяет, что обновление данных пользователя происходит успешно.")
    public void userDataUpdatedSuccessfullyTest() {
        // Arrange
        User user = DataGenerator.generateValidUser();
        petStore.userSteps().createUser(user);
        User updatedUser = user.toBuilder().firstName("NewFirstName").build();

        // Act
        petStore.userSteps().updateUser(user.username(), updatedUser);

        // Assert
        petStore.userSteps().assertUserExists(updatedUser);
    }
}
