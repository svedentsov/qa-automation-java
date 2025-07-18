package com.svedentsov.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Утилиты для работы с файлами")
class FileUtilTest {

    @TempDir
    Path tempDir;

    private final String cyrillicContent = "Привет, мир! Это тест.";
    private final String asciiContent = "Hello, world! This is a test.";

    @Test
    @DisplayName("Должен записывать и читать строку (UTF-8) корректно")
    void shouldWriteStringAndReadString() {
        Path file = tempDir.resolve("test-utf8.txt");
        FileUtil.writeString(file, cyrillicContent);

        assertTrue(Files.exists(file));
        String readContent = FileUtil.readString(file);
        assertEquals(cyrillicContent, readContent);
    }

    @Test
    @DisplayName("Должен записывать и читать строки (List) корректно")
    void shouldWriteAndReadLines() {
        Path file = tempDir.resolve("lines.txt");
        List<String> lines = List.of("Первая строка", "Вторая строка", "Третья строка с кириллицей");
        String content = String.join(System.lineSeparator(), lines);

        FileUtil.writeString(file, content);
        List<String> readLines = FileUtil.readLines(file);

        assertEquals(lines, readLines);
    }

    @Test
    @DisplayName("Должен записывать и читать байты корректно")
    void shouldWriteAndReadBytes() {
        Path file = tempDir.resolve("binary.dat");
        byte[] originalBytes = cyrillicContent.getBytes(StandardCharsets.UTF_8);

        FileUtil.writeBytes(file, originalBytes);
        byte[] readBytes = FileUtil.readBytes(file);

        assertArrayEquals(originalBytes, readBytes);
    }

    @Test
    @DisplayName("Должен дописывать контент в существующий файл")
    void shouldAppendContentToFile() {
        Path file = tempDir.resolve("append.log");
        FileUtil.writeString(file, "Начало. ", StandardCharsets.UTF_8);
        FileUtil.writeString(file, "Конец.", StandardCharsets.UTF_8, StandardOpenOption.APPEND);

        String content = FileUtil.readString(file, StandardCharsets.UTF_8);
        assertEquals("Начало. Конец.", content);
    }

    @Test
    @DisplayName("Должен корректно удалять файл")
    void shouldDeleteFile() {
        Path file = tempDir.resolve("to-delete.txt");
        assertDoesNotThrow(() -> FileUtil.writeString(file, "delete me"));
        assertTrue(Files.exists(file));

        assertTrue(FileUtil.deleteFile(file));
        assertFalse(Files.exists(file));
        // Повторное удаление должно быть безопасным
        assertFalse(FileUtil.deleteFile(file));
    }

    @Test
    @DisplayName("Должен рекурсивно удалять директорию с содержимым")
    void shouldDeleteDirectoryRecursively() throws IOException {
        Path mainDir = tempDir.resolve("main");
        Path subDir = mainDir.resolve("sub");
        Files.createDirectories(subDir);
        Files.createFile(mainDir.resolve("file1.txt"));
        Files.createFile(subDir.resolve("file2.txt"));

        assertTrue(Files.exists(mainDir));
        assertTrue(Files.exists(subDir));

        assertDoesNotThrow(() -> FileUtil.deleteDirectory(mainDir));

        assertFalse(Files.exists(mainDir));
        assertFalse(Files.exists(subDir));
    }

    @Test
    @DisplayName("Должен выбрасывать UncheckedIOException при чтении несуществующего файла")
    void shouldThrowExceptionWhenReadingNonExistentFile() {
        Path nonExistentFile = tempDir.resolve("non-existent.txt");
        assertThrows(UncheckedIOException.class, () -> FileUtil.readString(nonExistentFile));
    }

    @Test
    @DisplayName("Должен читать файл из ресурсов classpath")
    void shouldReadStringFromClasspath() {
        // Предполагается, что в src/test/resources есть файл resource-test.txt
        String content = FileUtil.readStringFromClasspath("resource-test.txt");
        assertEquals("Это тестовый файл из ресурсов.", content.trim());
    }

    @Test
    @DisplayName("Должен выбрасывать исключение, если ресурс в classpath не найден")
    void shouldThrowExceptionForNonExistentClasspathResource() {
        assertThrows(UncheckedIOException.class, () -> FileUtil.readStringFromClasspath("definitely-not-found.txt"));
    }
}
