package rest.service;

import rest.enums.PetStatus;
import rest.helper.RestExecutor;

import java.io.File;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.MULTIPART;
import static rest.enums.BaseUrl.PETSTORE;
import static rest.enums.Endpoints.*;

/**
 * Сервис для взаимодействия с API питомцев.
 */
public class PetService extends BaseService {
    /**
     * Добавление нового питомца.
     *
     * @param pet объект питомца, который нужно добавить
     * @return {@link RestExecutor} для управления запросом
     */
    public RestExecutor addPet(Object pet) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .setBody(pet);
        request.post(PET.path());
        return request;
    }

    /**
     * Получение информации о питомце по идентификатору.
     *
     * @param petId идентификатор питомца
     * @return {@link RestExecutor} для управления запросом
     */
    public RestExecutor getPetById(long petId) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .addPathParam("petId", String.valueOf(petId));
        request.get(PET_ID.path());
        return request;
    }

    /**
     * Удаление питомца по идентификатору.
     *
     * @param petId  идентификатор питомца
     * @param apiKey API ключ для аутентификации
     * @return {@link RestExecutor} для управления запросом
     */
    public RestExecutor deletePet(long petId, String apiKey) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .addHeader("api_key", apiKey)
                .addPathParam("petId", String.valueOf(petId));
        request.delete(PET_ID.path());
        return request;
    }

    /**
     * Поиск питомцев по статусу.
     *
     * @param status статус для поиска
     * @return {@link RestExecutor} для управления запросом
     */
    public RestExecutor findPetsByStatus(PetStatus status) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .addQueryParam("status", status.toString());
        request.get(PET_FIND_BY_STATUS.path());
        return request;
    }

    /**
     * Поиск питомцев по тегам.
     *
     * @param tags теги для поиска
     * @return {@link RestExecutor} для управления запросом
     */
    public RestExecutor findPetsByTags(String tags) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .addQueryParam("tags", tags);
        request.get(PET_FIND_BY_TAGS.path());
        return request;
    }

    /**
     * Обновление информации о питомце.
     *
     * @param pet объект питомца с обновленными данными
     * @return {@link RestExecutor} для управления запросом
     */
    public RestExecutor updatePet(Object pet) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .setBody(pet);
        request.put(PET.path());
        return request;
    }

    /**
     * Обновление информации о питомце с использованием формы.
     *
     * @param petId  идентификатор питомца
     * @param name   новое имя питомца
     * @param status новый статус питомца
     * @return {@link RestExecutor} для управления запросом
     */
    public RestExecutor updatePetWithForm(long petId, String name, PetStatus status) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(MULTIPART)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addPathParam("petId", String.valueOf(petId))
                .addFormParam("name", name)
                .addFormParam("status", status.toString());
        request.post(PET_ID.path());
        return request;
    }

    /**
     * Загрузка файла изображения для питомца.
     *
     * @param petId              идентификатор питомца
     * @param additionalMetadata дополнительная метаинформация
     * @param file               файл изображения для загрузки
     * @return {@link RestExecutor} для управления запросом
     */
    public RestExecutor uploadFile(long petId, String additionalMetadata, File file) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(MULTIPART)
                .addPathParam("petId", String.valueOf(petId))
                .addFormParam("additionalMetadata", additionalMetadata)
                .setSendFile(file.getName(), "application/octet-stream", file.getAbsolutePath());
        request.post(PET_UPLOAD_IMAGE.path());
        return request;
    }
}
