package rest.data;

import org.apache.commons.lang3.RandomStringUtils;
import rest.enums.OrderStatus;
import rest.enums.PetStatus;
import rest.model.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static rest.enums.PetStatus.AVAILABLE;

/**
 * Класс DataGenerator предназначен для генерации тестовых данных для различных моделей.
 * Он включает методы для создания случайных данных, таких как Pet, Order, User, и предоставляет
 * методы для создания как валидных, так и невалидных экземпляров этих объектов.
 */
public class DataGenerator {

    /**
     * Некорректный JSON для тестирования случаев с ошибками.
     */
    public static String INCORRECT_JSON = "{ \"id\": incorrectValue}";

    /**
     * Генерирует объект Pet с полным набором данных.
     */
    public static Pet generateFullDataPet() {
        return Pet.builder()
                .id(generateRandomId())
                .name(generateRandomString())
                .photoUrls(Arrays.asList(generateRandomString(), generateRandomString()))
                .category(Category.builder()
                        .id(generateRandomId())
                        .name(generateRandomString()).build())
                .tags(Arrays.asList(
                        Tag.builder().id(generateRandomId()).name(generateRandomString()).build(),
                        Tag.builder().id(generateRandomId()).name(generateRandomString()).build()))
                .status(AVAILABLE)
                .build();
    }

    /**
     * Генерирует объект Pet с минимальным набором данных.
     */
    public static Pet generateMinDataPet() {
        return Pet.builder()
                .id(generateRandomId())
                .name(generateRandomString())
                .build();
    }

    /**
     * Генерирует валидный объект Order.
     */
    public static Order generateValidOrder() {
        return Order.builder()
                .id(generateRandomId())
                .petId(1)
                .quantity(1)
                .shipDate(LocalDateTime.now())
                .status(OrderStatus.PLACED)
                .complete(false)
                .build();
    }

    /**
     * Генерирует валидного пользователя (User).
     */
    public static User generateValidUser() {
        return User.builder()
                .id(generateRandomId())
                .username(generateRandomUsername())
                .firstName(generateRandomString())
                .lastName(generateRandomString())
                .email(generateRandomEmail())
                .password(generateRandomPassword())
                .phone(generateRandomPhoneNumber())
                .userStatus(1)
                .build();
    }

    /**
     * Генерирует массив валидных пользователей.
     */
    public static List<User> generateValidUsersArray() {
        return generateUserList(3);
    }

    /**
     * Генерирует список валидных пользователей.
     */
    public static List<User> generateValidUsersList() {
        return generateUserList(3);
    }

    /**
     * Вспомогательный метод для генерации списка пользователей.
     *
     * @param count количество пользователей для генерации
     * @return список объектов User
     */
    private static List<User> generateUserList(int count) {
        return List.of(generateValidUser(), generateValidUser(), generateValidUser());
    }

    /**
     * Генерирует случайный идентификатор.
     */
    private static long generateRandomId() {
        return Math.abs(UUID.randomUUID().hashCode());
    }

    /**
     * Генерирует случайную строку.
     */
    private static String generateRandomString() {
        return RandomStringUtils.randomAlphabetic(7);
    }

    /**
     * Генерирует случайное имя пользователя.
     */
    private static String generateRandomUsername() {
        return "user" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Генерирует случайный адрес электронной почты.
     */
    private static String generateRandomEmail() {
        return generateRandomUsername() + "@example.com";
    }

    /**
     * Генерирует случайный пароль.
     */
    private static String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    /**
     * Генерирует случайный номер телефона.
     */
    private static String generateRandomPhoneNumber() {
        return RandomStringUtils.randomNumeric(10);
    }
}
