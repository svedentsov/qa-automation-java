package com.svedentsov.kafka.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;

/**
 * Утилитный класс для загрузки значений записей из файлов.
 */
@Slf4j
public class RecordLoader {

    /**
     * Загружает значение записи из указанного файла.
     *
     * @param file путь к файлу, из которого необходимо загрузить значение записи.
     * @return строковое представление значения записи.
     * @throws NullPointerException если указанный файл равен null.
     * @throws RuntimeException     если произошла ошибка ввода-вывода при загрузке файла.
     */
    public static String loadRecordValue(String file) {
        requireNonNull(file, "Файл не может быть null");
        try {
            log.info("Значение записи загружено из файла: {}", file);
            return new String(Files.readAllBytes(Paths.get(file)));
        } catch (IOException e) {
            log.error("Не удалось загрузить значение записи из файла", e);
            throw new RuntimeException("Не удалось загрузить значение записи из файла", e);
        }
    }
}
