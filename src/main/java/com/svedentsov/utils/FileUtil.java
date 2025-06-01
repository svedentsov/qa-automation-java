package com.svedentsov.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Утилитарный класс для работы с файлами.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {

    /**
     * Читает содержимое файла по указанному пути.
     *
     * @param filePath путь к файлу
     * @return содержимое файла в виде строки
     * @throws UncheckedIOException если возникает ошибка ввода-вывода
     */
    public static String readFile(String filePath) {
        try {
            return _readFile(filePath);
        } catch (IOException e) {
            throw new UncheckedIOException("Произошла ошибка при чтении файла " + filePath, e);
        }
    }

    /**
     * Читает содержимое файла по указанному пути.
     *
     * @param path путь к файлу
     * @return содержимое файла в виде строки
     * @throws UncheckedIOException если возникает ошибка ввода-вывода
     */
    public static String readFile(Path path) {
        try {
            return Files.readString(path, UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Произошла ошибка при чтении файла " + path, e);
        }
    }

    /**
     * Читает содержимое файла из входного потока.
     *
     * @param is входной поток данных
     * @return содержимое файла в виде строки
     * @throws UncheckedIOException если возникает ошибка ввода-вывода
     */
    public static String readFile(InputStream is) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            throw new UncheckedIOException("Произошла ошибка при чтении из InputStream", e);
        }
    }

    /**
     * Записывает текст в файл.
     *
     * @param file файл для записи
     * @param text текст для записи
     */
    public static void writeToFile(File file, String text) {
        try {
            Files.writeString(file.toPath(), text, UTF_8);
        } catch (IOException e) {
            log.warn("Произошла ошибка при записи в файл " + file.getName(), e);
        }
    }

    /**
     * Создает файл.
     *
     * @param file файл для создания
     * @return true, если файл был успешно создан; false, если файл уже существует
     * @throws UncheckedIOException если возникает ошибка ввода-вывода при создании файла
     */
    public static boolean createFile(File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            throw new UncheckedIOException("Произошла ошибка при создании файла " + file.getName(), e);
        }
    }

    /**
     * Создает или заменяет файл вместе с деревом каталогов.
     *
     * @param path путь к файлу
     * @throws UncheckedIOException если возникает ошибка ввода-вывода при создании файла
     */
    public static void createReplaceFileWithDirTree(Path path) {
        try {
            Files.createDirectories(path.getParent());
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            throw new UncheckedIOException("Произошла ошибка при создании файла " + path.toFile().getName(), e);
        }
    }

    /**
     * Создает или заменяет файлы вместе с деревом каталогов.
     *
     * @param files список файлов
     */
    public static void createReplaceFileWithDirTree(List<File> files) {
        files.forEach(file -> createReplaceFileWithDirTree(file.toPath()));
    }

    /**
     * Читает содержимое CSV-файла по указанному пути.
     *
     * @param filePath путь к CSV-файлу
     * @return список строк из CSV-файла (без строк, содержащих комментарии)
     * @throws UncheckedIOException если возникает ошибка ввода-вывода
     */
    public static List<String> readCsvFile(String filePath) {
        List<String> content = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath, Charset.defaultCharset()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("//")) {
                    content.add(String.join(StrUtil.COMMA, line));
                }
            }
            if (content.isEmpty()) {
                throw new IllegalStateException("Файл пуст!");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Произошла ошибка при чтении файла " + filePath, e);
        }
        return content;
    }

    /**
     * Читает содержимое файла по указанному пути и возвращает его в виде строки.
     *
     * @param pathToResource путь к ресурсу
     * @return содержимое файла в виде строки
     * @throws UncheckedIOException если возникает ошибка ввода-вывода
     */
    public static String readFileFormatted(String pathToResource) {
        var file = new File(Objects.requireNonNull(FileUtil.class.getClassLoader().getResource(pathToResource)).getFile());
        Path path = Paths.get(file.getAbsolutePath());
        try (Stream<String> lines = Files.lines(path, UTF_8)) {
            return lines.collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new UncheckedIOException("Произошла ошибка при чтении файла " + path, e);
        }
    }

    /**
     * Читает содержимое файла по указанному пути и возвращает его в виде строки.
     * Приватный метод для реализации чтения файла с использованием FileReader и BufferedReader.
     *
     * @param filePath путь к файлу
     * @return содержимое файла в виде строки
     * @throws IOException если возникает ошибка ввода-вывода
     */
    private static String _readFile(String filePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (Reader reader = new FileReader(new File(filePath), UTF_8);
             BufferedReader buffered = new BufferedReader(reader)) {

            String line;
            while ((line = buffered.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }
}
