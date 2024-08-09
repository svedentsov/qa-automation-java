package rest.steps;

import io.qameta.allure.Step;
import rest.enums.PetStatus;
import rest.model.ApiResponse;
import rest.model.Pet;

import java.io.File;
import java.util.List;

import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static rest.matcher.RestMatcher.contentType;
import static rest.matcher.RestMatcher.statusCode;

/**
 * Класс для выполнения шагов, связанных с операциями над питомцами в системе.
 * Содержит методы для создания, обновления, удаления и получения данных о питомцах.
 */
public class PetSteps extends BaseSteps {

    /**
     * Создает питомца с валидным запросом.
     *
     * @param pet данные питомца для создания
     * @return созданный питомец
     */
    @Step("Создание питомца c валидным запросом: {pet}")
    public Pet createPetSuccessfully(Pet pet) {
        return rest.petService()
                .addPet(pet)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200))
                .getResponseAs(Pet.class);
    }

    /**
     * Пытается создать питомца с невалидным запросом.
     *
     * @param pet данные питомца, содержащие ошибки
     * @return объект {@link PetSteps} для дальнейших действий
     */
    @Step("Создание питомца c невалидным запросом: {pet}")
    public PetSteps postBadRequest(Object pet) {
        rest.petService()
                .addPet(pet)
                .shouldHave(statusCode(400));
        return this;
    }

    /**
     * Получает питомца по идентификатору, ожидая, что питомец не найден.
     *
     * @param petId идентификатор питомца
     * @return объект {@link PetSteps} для дальнейших действий
     */
    @Step("Питомец не найден при получении по идентификатору: {petId}")
    public PetSteps getNotFoundPetById(long petId) {
        ApiResponse response = rest.petService()
                .getPetById(petId)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(404))
                .getResponseAs(ApiResponse.class);
        assertThat(response.message(), equalTo("Pet not found"));
        return this;
    }

    /**
     * Проверяет данные питомца.
     *
     * @param expectedPet ожидаемые данные питомца
     * @return объект {@link PetSteps} для дальнейших действий
     */
    @Step("Проверка полей питомца: {expectedPet}")
    public PetSteps assertPetData(Pet expectedPet) {
        Pet actualPet = rest.petService()
                .getPetById(expectedPet.id())
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200))
                .getResponseAs(Pet.class);
        assertThat(actualPet, equalTo(expectedPet));
        return this;
    }

    /**
     * Удаляет питомца по идентификатору.
     *
     * @param petId идентификатор питомца
     * @return объект {@link PetSteps} для дальнейших действий
     */
    @Step("Удалить питомца по идентификатору: {petId}")
    public PetSteps deletePetById(long petId) {
        rest.petService()
                .deletePet(petId, "your_api_key_here")
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200));
        return this;
    }

    /**
     * Пытается удалить несуществующего питомца по идентификатору.
     *
     * @param petId идентификатор несуществующего питомца
     * @return объект {@link PetSteps} для дальнейших действий
     */
    @Step("Попытка удалить несуществующего питомца по ID: {petId}")
    public PetSteps deleteNotFoundPetById(long petId) {
        rest.petService()
                .deletePet(petId, "your_api_key_here")
                .shouldHave(statusCode(404));
        return this;
    }

    /**
     * Обновляет данные питомца с валидным запросом.
     *
     * @param pet данные питомца для обновления
     * @return объект {@link PetSteps} для дальнейших действий
     */
    @Step("Обновление питомца успешно: {pet}")
    public PetSteps putPetSuccessfully(Pet pet) {
        rest.petService()
                .updatePet(pet)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200));
        return this;
    }

    /**
     * Пытается обновить питомца с невалидным запросом.
     *
     * @param pet данные питомца, содержащие ошибки
     * @return объект {@link PetSteps} для дальнейших действий
     */
    @Step("Попытка обновления недопустимого объекта питомца: {pet}")
    public PetSteps putBadRequest(Object pet) {
        rest.petService()
                .updatePet(pet)
                .shouldHave(statusCode(400));
        return this;
    }

    /**
     * Ищет питомцев по статусу.
     *
     * @param status статус питомцев для поиска
     * @return объект {@link PetSteps} для дальнейших действий
     */
    @Step("Поиск питомцев по статусу: {status}")
    public PetSteps findPetsByStatus(PetStatus status) {
        List<Pet> pets = rest.petService()
                .findPetsByStatus(status)
                .getResponseAsList(Pet.class);
        assertThat("Список питомцев по статусу '" + status + "' не должен быть пустым",
                pets, not(empty()));
        pets.forEach(pet -> assertThat("Питомец с ID " + pet.id() + " имеет статус '" + pet.status() + "', который не совпадает с ожидаемым '" + status + "'",
                pet.status(), equalTo(status)));
        return this;
    }

    /**
     * Обновляет данные питомца с использованием формы.
     *
     * @param pet данные питомца для обновления
     * @return объект {@link PetSteps} для дальнейших действий
     */
    @Step("Обновление питомца с использованием формы: {pet}")
    public PetSteps updatePetWithForm(Pet pet) {
        rest.petService()
                .updatePetWithForm(pet.id(), pet.name(), pet.status())
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200));
        return this;
    }

    /**
     * Загружает изображение для питомца.
     *
     * @param petId              идентификатор питомца
     * @param additionalMetadata дополнительные данные
     * @param file               файл изображения для загрузки
     * @return объект {@link PetSteps} для дальнейших действий
     */
    @Step("Загрузка изображение для питомца: {petId}, {additionalMetadata}, {file}")
    public PetSteps uploadFile(long petId, String additionalMetadata, File file) {
        rest.petService()
                .uploadFile(petId, additionalMetadata, file)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200));
        return this;
    }

    /**
     * Ищет питомцев по тегам.
     *
     * @param tags теги для поиска питомцев
     * @return список найденных питомцев
     */
    @Step("Поиск питомцев по тегам: {tags}")
    public List<Pet> findPetsByTags(String tags) {
        return rest.petService()
                .findPetsByTags(tags)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200))
                .getResponseAsList(Pet.class);
    }
}
