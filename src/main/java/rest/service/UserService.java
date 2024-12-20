package rest.service;

import rest.helper.RestExecutor;
import rest.model.User;

import java.util.List;

import static io.restassured.http.ContentType.JSON;
import static rest.enums.BaseUrl.PETSTORE;
import static rest.enums.Endpoints.*;

/**
 * Сервис для взаимодействия с API пользователей.
 */
public class UserService extends BaseService {
    /**
     * Создание нового пользователя.
     *
     * @param body объект пользователя, который необходимо создать
     * @return объект {@link RestExecutor} для выполнения запроса
     */
    public RestExecutor createUser(User body) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .setBody(body);
        request.post(USER.path());
        return request;
    }

    /**
     * Создание списка пользователей с использованием массива.
     *
     * @param body список объектов пользователей, которые необходимо создать
     * @return объект {@link RestExecutor} для выполнения запроса
     */
    public RestExecutor createUsersWithArrayInput(List<User> body) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .setBody(body);
        request.post(USER_CREATE_WITH_ARRAY.path());
        return request;
    }

    /**
     * Создание списка пользователей с использованием списка.
     *
     * @param body список объектов пользователей, которые необходимо создать
     * @return объект {@link RestExecutor} для выполнения запроса
     */
    public RestExecutor createUsersWithListInput(List<User> body) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .setBody(body);
        request.post(USER_CREATE_WITH_LIST.path());
        return request;
    }

    /**
     * Удаление пользователя по имени пользователя.
     *
     * @param username имя пользователя, которого необходимо удалить
     * @return объект {@link RestExecutor} для выполнения запроса
     */
    public RestExecutor deleteUser(String username) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .addPathParam("username", username);
        request.delete(USER_BY_USERNAME.path());
        return request;
    }

    /**
     * Получение информации о пользователе по имени пользователя.
     *
     * @param username имя пользователя, информацию о котором необходимо получить
     * @return объект {@link RestExecutor} для выполнения запроса
     */
    public RestExecutor getUserByName(String username) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .addPathParam("username", username);
        request.get(USER_BY_USERNAME.path());
        return request;
    }

    /**
     * Вход пользователя в систему.
     *
     * @param username имя пользователя для входа
     * @param password пароль пользователя для входа
     * @return объект {@link RestExecutor} для выполнения запроса
     */
    public RestExecutor loginUser(String username, String password) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .addQueryParam("username", username)
                .addQueryParam("password", password);
        request.get(USER_LOGIN.path());
        return request;
    }

    /**
     * Выход из текущей сессии пользователя.
     *
     * @return объект {@link RestExecutor} для выполнения запроса
     */
    public RestExecutor logoutUser() {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON);
        request.get(USER_LOGOUT.path());
        return request;
    }

    /**
     * Обновление информации о пользователе.
     *
     * @param username имя пользователя, информацию о котором необходимо обновить
     * @param body     объект пользователя с обновленными данными
     * @return объект {@link RestExecutor} для выполнения запроса
     */
    public RestExecutor updateUser(String username, User body) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .addPathParam("username", username)
                .setBody(body);
        request.put(USER_BY_USERNAME.path());
        return request;
    }
}
