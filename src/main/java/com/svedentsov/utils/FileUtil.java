package com.svedentsov.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Утилитарный класс для выполнения файловых операций.
 * <p>Класс построен на основе современного API {@code java.nio.file} для обеспечения
 * высокой производительности и надежной обработки ошибок. Все исключения типа
 * {@link IOException} перехватываются и выбрасываются как {@link UncheckedIOException}.
 */
@Slf4j
@UtilityClass
public class FileUtil {

    /**
     * Читает все содержимое файла в строку, используя кодировку UTF-8.
     *
     * @param path Путь к файлу. Не может быть {@code null}.
     * @return Содержимое файла в виде строки.
     * @throws UncheckedIOException если происходит ошибка ввода-вывода.
     */
    public static String readString(final Path path) {
        return readString(path, StandardCharsets.UTF_8);
    }

    /**
     * Читает все содержимое файла в строку, используя указанную кодировку.
     *
     * @param path    Путь к файлу. Не может быть {@code null}.
     * @param charset Кодировка, используемая для чтения файла.
     * @return Содержимое файла в виде строки.
     * @throws UncheckedIOException если происходит ошибка ввода-вывода.
     */
    public static String readString(final Path path, final Charset charset) {
        Objects.requireNonNull(path, "Путь к файлу не может быть null");
        try {
            return Files.readString(path, charset);
        } catch (IOException e) {
            log.error("Ошибка чтения файла '{}': {}", path, e.getMessage());
            throw new UncheckedIOException("Ошибка чтения файла: " + path, e);
        }
    }

    /**
     * Читает все строки из файла в список, используя кодировку UTF-8.
     *
     * @param path Путь к файлу. Не может быть {@code null}.
     * @return Список строк из файла.
     * @throws UncheckedIOException если происходит ошибка ввода-вывода.
     */
    public static List<String> readLines(final Path path) {
        return readLines(path, StandardCharsets.UTF_8);
    }

    /**
     * Читает все строки из файла в список, используя указанную кодировку.
     *
     * @param path    Путь к файлу. Не может быть {@code null}.
     * @param charset Кодировка, используемая для чтения файла.
     * @return Список строк из файла.
     * @throws UncheckedIOException если происходит ошибка ввода-вывода.
     */
    public static List<String> readLines(final Path path, final Charset charset) {
        Objects.requireNonNull(path, "Путь к файлу не может быть null");
        try {
            return Files.readAllLines(path, charset);
        } catch (IOException e) {
            log.error("Ошибка чтения строк из файла '{}': {}", path, e.getMessage());
            throw new UncheckedIOException("Ошибка чтения строк из файла: " + path, e);
        }
    }

    /**
     * Читает все байты из файла.
     *
     * @param path Путь к файлу. Не может быть {@code null}.
     * @return Массив байтов с содержимым файла.
     * @throws UncheckedIOException если происходит ошибка ввода-вывода.
     */
    public static byte[] readBytes(final Path path) {
        Objects.requireNonNull(path, "Путь к файлу не может быть null");
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("Ошибка чтения байтов из файла '{}': {}", path, e.getMessage());
            throw new UncheckedIOException("Ошибка чтения байтов из файла: " + path, e);
        }
    }

    /**
     * Читает строковый ресурс из classpath (например, из {@code src/main/resources}).
     *
     * @param resourceName Имя ресурса (например, "sql/my-query.sql"). Не может быть {@code null}.
     * @return Содержимое ресурса в виде строки.
     * @throws UncheckedIOException     если ресурс не найден или произошла ошибка чтения.
     * @throws IllegalArgumentException если имя ресурса некорректно.
     */
    public static String readStringFromClasspath(final String resourceName) {
        Objects.requireNonNull(resourceName, "Имя ресурса не может быть null");
        try {
            URL resourceUrl = FileUtil.class.getClassLoader().getResource(resourceName);
            if (resourceUrl == null) {
                throw new IOException("Ресурс не найден в classpath: " + resourceName);
            }
            return readString(Paths.get(resourceUrl.toURI()));
        } catch (IOException e) {
            log.error("Ошибка чтения ресурса '{}' из classpath: {}", resourceName, e.getMessage());
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            log.error("Некорректный синтаксис URI для ресурса '{}': {}", resourceName, e.getMessage());
            throw new IllegalArgumentException("Некорректный синтаксис URI для ресурса", e);
        }
    }

    /**
     * Записывает строку в файл, используя кодировку UTF-8. Если файл существует, он будет перезаписан.
     *
     * @param path    Путь к файлу. Не может быть {@code null}.
     * @param content Строка для записи. Не может быть {@code null}.
     * @throws UncheckedIOException если происходит ошибка ввода-вывода.
     */
    public static void writeString(final Path path, final CharSequence content) {
        writeString(path, content, StandardCharsets.UTF_8);
    }

    /**
     * Записывает строку в файл, используя указанную кодировку.
     *
     * @param path      Путь к файлу. Не может быть {@code null}.
     * @param content   Строка для записи. Не может быть {@code null}.
     * @param charset   Кодировка для записи.
     * @param options   Опции открытия файла (например, {@code StandardOpenOption.APPEND}).
     * @throws UncheckedIOException если происходит ошибка ввода-вывода.
     */
    public static void writeString(final Path path, final CharSequence content, final Charset charset, final OpenOption... options) {
        Objects.requireNonNull(path, "Путь к файлу не может быть null");
        Objects.requireNonNull(content, "Содержимое для записи не может быть null");
        try {
            // Убедимся, что родительские директории существуют
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, charset, options);
        } catch (IOException e) {
            log.error("Ошибка записи в файл '{}': {}", path, e.getMessage());
            throw new UncheckedIOException("Ошибка записи в файл: " + path, e);
        }
    }

    /**
     * Записывает массив байтов в файл. Если файл существует, он будет перезаписан.
     *
     * @param path    Путь к файлу. Не может быть {@code null}.
     * @param bytes   Массив байтов для записи. Не может быть {@code null}.
     * @param options Опции открытия файла.
     * @throws UncheckedIOException если происходит ошибка ввода-вывода.
     */
    public static void writeBytes(final Path path, final byte[] bytes, final OpenOption... options) {
        Objects.requireNonNull(path, "Путь к файлу не может быть null");
        Objects.requireNonNull(bytes, "Массив байтов для записи не может быть null");
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, bytes, options);
        } catch (IOException e) {
            log.error("Ошибка записи байтов в файл '{}': {}", path, e.getMessage());
            throw new UncheckedIOException("Ошибка записи байтов в файл: " + path, e);
        }
    }

    /**
     * Удаляет файл, если он существует.
     *
     * @param path Путь к файлу.
     * @return {@code true}, если файл был удален, иначе {@code false}.
     * @throws UncheckedIOException если происходит ошибка ввода-вывода.
     */
    public static boolean deleteFile(final Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Ошибка удаления файла '{}': {}", path, e.getMessage());
            throw new UncheckedIOException("Ошибка удаления файла: " + path, e);
        }
    }

    /**
     * Рекурсивно удаляет директорию со всем ее содержимым.
     *
     * @param dirPath Путь к директории для удаления.
     * @throws UncheckedIOException если директория не существует или происходит ошибка ввода-вывода.
     */
    public static void deleteDirectory(final Path dirPath) {
        Objects.requireNonNull(dirPath, "Путь к директории не может быть null");
        if (!Files.isDirectory(dirPath)) {
            log.warn("Попытка удалить не директорию: {}", dirPath);
            return;
        }
        try (Stream<Path> walk = Files.walk(dirPath)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(FileUtil::deleteFile);
        } catch (IOException e) {
            log.error("Ошибка при рекурсивном удалении директории '{}': {}", dirPath, e.getMessage());
            throw new UncheckedIOException("Ошибка удаления директории: " + dirPath, e);
        }
    }
}
