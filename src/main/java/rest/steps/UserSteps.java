package rest.steps;

import io.qameta.allure.Step;
import rest.model.User;

import java.util.List;

import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static rest.matcher.RestMatcher.contentType;
import static rest.matcher.RestMatcher.statusCode;

/**
 * Класс для выполнения шагов, связанных с операциями над пользователями.
 * Содержит методы для создания, получения, обновления и удаления пользователей.
 */
public class UserSteps extends BaseSteps {

    /**
     * Создаёт нового пользователя.
     *
     * @param body тело запроса, содержащее данные пользователя
     * @return текущий объект {@link UserSteps} для дальнейших действий
     */
    @Step("Создание пользователя: {body}")
    public UserSteps createUser(User body) {
        rest.userService()
                .createUser(body)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200));
        return this;
    }

    /**
     * Создаёт нескольких пользователей из массива.
     *
     * @param body список пользователей
     * @return текущий объект {@link UserSteps} для дальнейших действий
     */
    @Step("Создание пользователей с массивом ввода: {body}")
    public UserSteps createUsersWithArrayInput(List<User> body) {
        rest.userService()
                .createUsersWithArrayInput(body)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200));
        return this;
    }

    /**
     * Создаёт нескольких пользователей из списка.
     *
     * @param body список пользователей
     * @return текущий объект {@link UserSteps} для дальнейших действий
     */
    @Step("Создание пользователей с списком ввода: {body}")
    public UserSteps createUsersWithListInput(List<User> body) {
        rest.userService()
                .createUsersWithListInput(body)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200));
        return this;
    }

    /**
     * Удаляет пользователя по имени.
     *
     * @param username имя пользователя
     * @return текущий объект {@link UserSteps} для дальнейших действий
     */
    @Step("Удаление пользователя с именем: {username}")
    public UserSteps deleteUser(String username) {
        rest.userService()
                .deleteUser(username)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200));
        return this;
    }

    /**
     * Получает пользователя по имени.
     *
     * @param username имя пользователя
     * @return объект User, представляющий найденного пользователя
     */
    @Step("Получение пользователя по имени: {username}")
    public User getUserByName(String username) {
        return rest.userService()
                .getUserByName(username)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200))
                .getResponseAs(User.class);
    }

    /**
     * Проверяет, что пользователь не существует.
     *
     * @param username имя пользователя
     * @return текущий объект {@link UserSteps} для дальнейших действий
     */
    @Step("Проверка, что пользователь не существует: {username}")
    public UserSteps assertUserNotFound(String username) {
        rest.userService()
                .getUserByName(username)
                .shouldHave(statusCode(404));
        return this;
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param username имя пользователя
     * @param password пароль пользователя
     * @return строка с токеном сессии или другим подтверждением входа
     */
    @Step("Вход пользователя в систему: {username}")
    public String loginUser(String username, String password) {
        return rest.userService()
                .loginUser(username, password)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200))
                .getResponseAsString();
    }

    /**
     * Выполняет выход пользователя из системы.
     *
     * @return текущий объект {@link UserSteps} для дальнейших действий
     */
    @Step("Выход из системы")
    public UserSteps logoutUser() {
        rest.userService()
                .logoutUser()
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200));
        return this;
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param username имя пользователя.
     * @param body     тело запроса с обновленными данными
     * @return текущий объект {@link UserSteps} для дальнейших действий
     */
    @Step("Обновление пользователя с именем: {username}")
    public UserSteps updateUser(String username, User body) {
        rest.userService()
                .updateUser(username, body)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200));
        return this;
    }

    /**
     * Проверяет, что данные пользователя соответствуют ожидаемым.
     *
     * @param expected ожидаемые данные пользователя
     * @param actual   фактические данные пользователя
     */
    @Step("Проверка данных пользователя")
    public void assertUserData(User expected, User actual) {
        assertEquals(expected, actual, "Данные пользователя не соответствуют ожидаемым.");
    }

    /**
     * Проверяет существование пользователя.
     *
     * @param user объект User для проверки
     */
    @Step("Проверка существования пользователя: {user}")
    public void assertUserExists(User user) {
        User fetchedUser = getUserByName(user.username());
        assertUserData(user, fetchedUser);
    }
}
