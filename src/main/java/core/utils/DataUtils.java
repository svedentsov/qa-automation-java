package core.utils;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;

/**
 * Утилитарный класс для генерации случайных данных с использованием библиотеки JavaFaker.
 */
public class DataUtils {

    private static final Faker faker = new Faker();

    /**
     * Генерирует случайное имя пользователя.
     *
     * @return Случайное имя пользователя
     */
    @Nonnull
    public static String generateRandomUsername() {
        return faker.name().username();
    }

    /**
     * Генерирует случайный пароль.
     *
     * @return случайный пароль
     */
    @Nonnull
    public static String generateRandomPassword() {
        return faker.bothify("????####");
    }

    /**
     * Генерирует случайное имя.
     *
     * @return случайное имя
     */
    @Nonnull
    public static String generateRandomName() {
        return faker.name().firstName();
    }

    /**
     * Генерирует случайную фамилию.
     *
     * @return случайная фамилия
     */
    @Nonnull
    public static String generateRandomSurname() {
        return faker.name().lastName();
    }

    /**
     * Генерирует новую категорию.
     *
     * @return новая категория
     */
    @Nonnull
    public static String generateNewCategory() {
        return faker.food().fruit();
    }

    /**
     * Генерирует случайное предложение с указанным количеством слов.
     *
     * @param wordsCount количество слов в предложении
     * @return случайное предложение
     */
    @Nonnull
    public static String generateRandomSentence(int wordsCount) {
        return faker.lorem().sentence(wordsCount);
    }
}
