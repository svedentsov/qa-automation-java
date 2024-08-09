package core.utils;

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
     * @param filePath Путь к файлу.
     * @return Содержимое файла в виде строки.
     * @throws IllegalStateException Если возникает ошибка ввода-вывода.
     */
    public static String readFile(String filePath) {
        try {
            return _readFile(filePath);
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while reading from file " + filePath, e);
        }
    }

    /**
     * Читает содержимое файла по указанному пути.
     *
     * @param path Путь к файлу.
     * @return Содержимое файла в виде строки.
     * @throws IllegalStateException Если возникает ошибка ввода-вывода.
     */
    public static String readFile(Path path) {
        try {
            return Files.readString(path, UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while reading from file " + path, e);
        }
    }

    /**
     * Читает содержимое файла из входного потока.
     *
     * @param is Входной поток данных.
     * @return Содержимое файла в виде строки.
     * @throws RuntimeException Если возникает ошибка ввода-вывода.
     */
    public static String readFile(InputStream is) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while reading from InputStream " + e.getMessage(), e);
        }
    }

    /**
     * Записывает текст в файл.
     *
     * @param file Файл для записи.
     * @param text Текст для записи.
     */
    public static void writeToFile(File file, String text) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.print(text);
        } catch (IOException e) {
            log.warn("Error occurred while writing into file " + file.getName(), e);
        }
    }

    /**
     * Создает файл.
     *
     * @param file Файл для создания.
     * @return true, если файл был успешно создан; false, если файл уже существует.
     * @throws IllegalStateException Если возникает ошибка ввода-вывода при создании файла.
     */
    public static boolean createFile(File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while creating file " + file.getName(), e);
        }
    }

    /**
     * Создает или заменяет файл вместе с деревом каталогов.
     *
     * @param path Путь к файлу.
     * @throws IllegalStateException Если возникает ошибка ввода-вывода при создании файла.
     */
    public static void createReplaceFileWithDirTree(Path path) {
        try {
            Files.createDirectories(path.getParent());
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while creating file " + path.toFile().getName(), e);
        }
    }

    /**
     * Создает или заменяет файлы вместе с деревом каталогов.
     *
     * @param files Список файлов.
     */
    public static void createReplaceFileWithDirTree(List<File> files) {
        files.forEach(file -> createReplaceFileWithDirTree(file.toPath()));
    }

    /**
     * Читает содержимое CSV-файла по указанному пути.
     *
     * @param filePath Путь к CSV-файлу.
     * @return Список строк из CSV-файла (без строк, содержащих комментарии).
     * @throws IllegalStateException Если возникает ошибка ввода-вывода.
     */
    public static List<String> readCsvFile(String filePath) {
        List<String> content = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath, Charset.defaultCharset()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("//")) {
                    content.add(String.join(StringUtil.COMMA, line));
                }
            }
            if (content.isEmpty()) {
                throw new IllegalStateException("File is empty!");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while reading file " + filePath, e);
        }
        return content;
    }

    /**
     * Читает содержимое файла по указанному пути и возвращает его в виде строки.
     *
     * @param pathToResource Путь к ресурсу.
     * @return Содержимое файла в виде строки.
     * @throws IllegalStateException Если возникает ошибка ввода-вывода.
     */
    public static String readFileFormatted(String pathToResource) {
        var file = new File(Objects.requireNonNull(FileUtil.class.getClassLoader().getResource(pathToResource)).getFile());
        Path path = Paths.get(file.getAbsolutePath());
        try (Stream<String> lines = Files.lines(path)) {
            return lines.collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while reading from file " + path, e);
        }
    }

    /**
     * Читает содержимое файла по указанному пути и возвращает его в виде строки.
     * Приватный метод для реализации чтения файла с использованием FileReader и BufferedReader.
     *
     * @param filePath Путь к файлу.
     * @return Содержимое файла в виде строки.
     * @throws IOException Если возникает ошибка ввода-вывода.
     */
    private static String _readFile(String filePath) throws IOException {
        String fileOutput;
        StringBuilder stringBuilder = new StringBuilder();

        try (Reader reader = new FileReader(new File(filePath));
             BufferedReader buffered = new BufferedReader(reader)) {

            String line;
            while ((line = buffered.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(StringUtil.EMPTY);
            }

            fileOutput = stringBuilder.toString();
        }
        return fileOutput;
    }
}
