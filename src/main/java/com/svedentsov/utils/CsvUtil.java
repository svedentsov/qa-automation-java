package com.svedentsov.utils;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Утилитарный класс для сериализации и десериализации объектов в формат CSV.
 * <p>Класс предоставляет гибкие методы для работы с CSV-файлами и строками,
 * позволяя настраивать параметры формата, такие как разделитель, наличие заголовка и кодировку.
 * В основе лежит библиотека Jackson Dataformat CSV, что обеспечивает высокую производительность
 * и совместимость с существующей экосистемой проекта.
 */
@Slf4j
@UtilityClass
public class CsvUtil {

    private static final CsvMapper CSV_MAPPER = createDefaultMapper();

    /**
     * Создает и конфигурирует стандартный экземпляр CsvMapper.
     *
     * @return настроенный {@link CsvMapper}
     */
    private static CsvMapper createDefaultMapper() {
        CsvMapper mapper = new CsvMapper();
        // Для совместимости с датами из Java 8+, если они будут в объектах
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    // Сериализация (запись в CSV)

    /**
     * Сериализует список объектов в CSV-строку с использованием конфигурации по умолчанию.
     *
     * @param data Список объектов для сериализации. Не может быть {@code null}.
     * @param type Класс объектов в списке. Не может быть {@code null}.
     * @param <T>  Тип объектов.
     * @return Строка в формате CSV.
     * @throws CsvOperationException если происходит ошибка сериализации.
     */
    public static <T> String toString(final List<T> data, final Class<T> type) {
        return toString(data, type, CsvConfig.defaults());
    }

    /**
     * Сериализует список объектов в CSV-строку с использованием заданной конфигурации.
     *
     * @param data   Список объектов для сериализации. Не может быть {@code null}.
     * @param type   Класс объектов в списке. Не может быть {@code null}.
     * @param config Конфигурация для сериализации. Не может быть {@code null}.
     * @param <T>    Тип объектов.
     * @return Строка в формате CSV.
     * @throws CsvOperationException если происходит ошибка сериализации.
     */
    public static <T> String toString(final List<T> data, final Class<T> type, final CsvConfig config) {
        Objects.requireNonNull(data, "Список данных для сериализации не может быть null");
        Objects.requireNonNull(type, "Класс данных для сериализации не может быть null");
        Objects.requireNonNull(config, "Конфигурация CSV не может быть null");

        if (data.isEmpty()) {
            return "";
        }

        try (StringWriter stringWriter = new StringWriter()) {
            CsvSchema schema = buildSchema(type, config);
            ObjectWriter writer = CSV_MAPPER.writer(schema);
            writer.writeValue(stringWriter, data);
            return stringWriter.toString();
        } catch (IOException e) {
            log.error("Ошибка сериализации объектов типа '{}' в CSV строку: {}", type.getSimpleName(), e.getMessage());
            throw new CsvOperationException("Ошибка сериализации в CSV строку", e);
        }
    }

    /**
     * Записывает список объектов в CSV-файл с использованием конфигурации по умолчанию (UTF-8, с заголовком, разделитель ',').
     *
     * @param path Путь к файлу для записи. Не может быть {@code null}.
     * @param data Список объектов для записи. Не может быть {@code null}.
     * @param type Класс объектов в списке. Не может быть {@code null}.
     * @param <T>  Тип объектов.
     * @throws UncheckedIOException  если происходит ошибка ввода-вывода.
     * @throws CsvOperationException если происходит ошибка сериализации.
     */
    public static <T> void write(final Path path, final List<T> data, final Class<T> type) {
        write(path, data, type, CsvConfig.defaults());
    }

    /**
     * Записывает список объектов в CSV-файл с использованием заданной конфигурации.
     *
     * @param path   Путь к файлу для записи. Не может быть {@code null}.
     * @param data   Список объектов для записи. Не может быть {@code null}.
     * @param type   Класс объектов в списке. Не может быть {@code null}.
     * @param config Конфигурация для записи. Не может быть {@code null}.
     * @param <T>    Тип объектов.
     * @throws UncheckedIOException  если происходит ошибка ввода-вывода.
     * @throws CsvOperationException если происходит ошибка сериализации.
     */
    public static <T> void write(final Path path, final List<T> data, final Class<T> type, final CsvConfig config) {
        Objects.requireNonNull(path, "Путь к файлу не может быть null");
        String csvContent = toString(data, type, config);
        FileUtil.writeString(path, csvContent, config.getCharset());
    }


    // Десериализация (чтение из CSV)

    /**
     * Десериализует CSV-файл в список объектов с использованием конфигурации по умолчанию.
     *
     * @param path Путь к CSV-файлу. Не может быть {@code null}.
     * @param type Класс целевых объектов. Не может быть {@code null}.
     * @param <T>  Тип объектов.
     * @return Список объектов, прочитанных из файла.
     * @throws CsvOperationException если происходит ошибка десериализации.
     * @throws UncheckedIOException  если происходит ошибка чтения файла.
     */
    public static <T> List<T> read(final Path path, final Class<T> type) {
        return read(path, type, CsvConfig.defaults());
    }

    /**
     * Десериализует CSV-файл в список объектов с использованием заданной конфигурации.
     *
     * @param path   Путь к CSV-файлу. Не может быть {@code null}.
     * @param type   Класс целевых объектов. Не может быть {@code null}.
     * @param config Конфигурация для чтения. Не может быть {@code null}.
     * @param <T>    Тип объектов.
     * @return Список объектов, прочитанных из файла.
     * @throws CsvOperationException если происходит ошибка десериализации.
     * @throws UncheckedIOException  если происходит ошибка чтения файла.
     */
    public static <T> List<T> read(final Path path, final Class<T> type, final CsvConfig config) {
        Objects.requireNonNull(path, "Путь к файлу не может быть null");
        try {
            String csvContent = Files.readString(path, config.getCharset());
            return read(csvContent, type, config);
        } catch (IOException e) {
            log.error("Ошибка чтения CSV файла '{}': {}", path, e.getMessage());
            throw new UncheckedIOException("Ошибка чтения файла: " + path, e);
        }
    }

    /**
     * Десериализует CSV-строку в список объектов с использованием заданной конфигурации.
     *
     * @param csvContent Содержимое CSV в виде строки. Не может быть {@code null}.
     * @param type       Класс целевых объектов. Не может быть {@code null}.
     * @param config     Конфигурация для чтения. Не может быть {@code null}.
     * @param <T>        Тип объектов.
     * @return Список объектов, прочитанных из строки.
     * @throws CsvOperationException если происходит ошибка десериализации.
     */
    public static <T> List<T> read(final String csvContent, final Class<T> type, final CsvConfig config) {
        Objects.requireNonNull(csvContent, "Содержимое CSV не может быть null");
        Objects.requireNonNull(type, "Класс для десериализации не может быть null");
        Objects.requireNonNull(config, "Конфигурация CSV не может быть null");

        try {
            CsvSchema schema = buildSchema(type, config);
            ObjectReader reader = CSV_MAPPER.readerFor(type).with(schema);
            return reader.<T>readValues(csvContent).readAll();
        } catch (IOException e) {
            log.error("Ошибка десериализации CSV в объекты типа '{}': {}", type.getSimpleName(), e.getMessage());
            throw new CsvOperationException("Ошибка десериализации из CSV", e);
        }
    }

    // Вспомогательные методы и классы

    /**
     * Строит схему {@link CsvSchema} на основе класса и конфигурации.
     */
    private static <T> CsvSchema buildSchema(Class<T> type, CsvConfig config) {
        CsvSchema schema = CSV_MAPPER.schemaFor(type);
        if (config.isUseHeader()) {
            schema = schema.withHeader();
        } else {
            schema = schema.withoutHeader();
        }
        return schema.withColumnSeparator(config.getSeparator())
                .withQuoteChar(config.getQuoteChar())
                .withLineSeparator("\n"); // Стандартизируем для консистентности
    }

    /**
     * Конфигурационный класс для операций с CSV.
     * Используйте {@link CsvConfig#builder()} для создания кастомной конфигурации
     * или {@link CsvConfig#defaults()} для получения настроек по умолчанию.
     */
    @Getter
    @Builder(toBuilder = true)
    public static class CsvConfig {
        /**
         * Разделитель столбцов. По умолчанию: ','.
         */
        @Builder.Default
        private final char separator = ',';

        /**
         * Символ для экранирования значений. По умолчанию: '"'.
         */
        @Builder.Default
        private final char quoteChar = '"';

        /**
         * Использовать ли первую строку как заголовок. По умолчанию: true.
         */
        @Builder.Default
        private final boolean useHeader = true;

        /**
         * Кодировка файла. По умолчанию: UTF-8.
         */
        @Builder.Default
        private final Charset charset = StandardCharsets.UTF_8;

        /**
         * Возвращает экземпляр с настройками по умолчанию.
         *
         * @return Конфигурация по умолчанию.
         */
        public static CsvConfig defaults() {
            return CsvConfig.builder().build();
        }
    }

    /**
     * Исключение, выбрасываемое при ошибках в ходе операций с CSV.
     */
    public static final class CsvOperationException extends RuntimeException {
        public CsvOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}