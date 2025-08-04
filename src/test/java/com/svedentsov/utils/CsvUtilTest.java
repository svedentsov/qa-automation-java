package com.svedentsov.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Комплексные тесты для утилитарного класса {@link CsvUtil}.
 */
@DisplayName("Тестирование утилиты для работы с CSV (CsvUtil)")
class CsvUtilTest {

    private record Person(String name, int age, String city, LocalDate birthDate) {
    }

    private final List<Person> testPersons = List.of(
            new Person("John Doe", 30, "New York", LocalDate.of(1994, 5, 15)),
            new Person("Jane Smith", 25, "London", LocalDate.of(1999, 8, 22)),
            new Person("Peter Jones", 42, "Berlin, Germany", LocalDate.of(1982, 1, 10))
    );

    @Nested
    @DisplayName("Сериализация (объект -> CSV строка)")
    class SerializationTests {
        @Test
        @DisplayName("Должен корректно сериализовать список объектов в строку с настройками по умолчанию")
        void shouldSerializeWithDefaultConfig() {
            String expectedCsv = """
                    name,age,city,birthDate
                    John Doe,30,"New York",1994-05-15
                    Jane Smith,25,London,1999-08-22
                    "Peter Jones",42,"Berlin, Germany",1982-01-10
                    """;

            String actualCsv = CsvUtil.toString(testPersons, Person.class);
            // Сравниваем, игнорируя различия в окончаниях строк (CRLF vs LF)
            assertThat(actualCsv).isEqualToNormalizingNewlines(expectedCsv);
        }

        @Test
        @DisplayName("Должен корректно сериализовать с кастомным разделителем (точка с запятой)")
        void shouldSerializeWithSemicolonSeparator() {
            String expectedCsv = """
                    name;age;city;birthDate
                    "John Doe";30;"New York";1994-05-15
                    "Jane Smith";25;London;1999-08-22
                    "Peter Jones";42;"Berlin, Germany";1982-01-10
                    """;
            CsvUtil.CsvConfig config = CsvUtil.CsvConfig.builder().separator(';').build();
            String actualCsv = CsvUtil.toString(testPersons, Person.class, config);
            assertThat(actualCsv).isEqualToNormalizingNewlines(expectedCsv);
        }

        @Test
        @DisplayName("Должен корректно сериализовать без заголовка")
        void shouldSerializeWithoutHeader() {
            String expectedCsv = """
                    John Doe,30,"New York",1994-05-15
                    Jane Smith,25,London,1999-08-22
                    "Peter Jones",42,"Berlin, Germany",1982-01-10
                    """;
            CsvUtil.CsvConfig config = CsvUtil.CsvConfig.builder().useHeader(false).build();
            String actualCsv = CsvUtil.toString(testPersons, Person.class, config);
            assertThat(actualCsv).isEqualToNormalizingNewlines(expectedCsv);
        }

        @Test
        @DisplayName("Должен возвращать пустую строку для пустого списка")
        void shouldReturnEmptyStringForEmptyList() {
            String actualCsv = CsvUtil.toString(Collections.emptyList(), Person.class);
            assertThat(actualCsv).isEmpty();
        }
    }

    @Nested
    @DisplayName("Десериализация (CSV строка -> объект)")
    class DeserializationTests {
        @Test
        @DisplayName("Должен корректно десериализовать строку в список объектов с заголовком")
        void shouldDeserializeWithHeader() {
            String csvContent = """
                    name,age,city,birthDate
                    John Doe,30,"New York",1994-05-15
                    Jane Smith,25,London,1999-08-22
                    "Peter Jones",42,"Berlin, Germany",1982-01-10
                    """;
            List<Person> actualPersons = CsvUtil.read(csvContent, Person.class, CsvUtil.CsvConfig.defaults());
            assertThat(actualPersons).containsExactlyElementsOf(testPersons);
        }

        @Test
        @DisplayName("Должен корректно десериализовать строку без заголовка")
        void shouldDeserializeWithoutHeader() {
            String csvContent = """
                    John Doe,30,"New York",1994-05-15
                    Jane Smith,25,London,1999-08-22
                    "Peter Jones",42,"Berlin, Germany",1982-01-10
                    """;
            CsvUtil.CsvConfig config = CsvUtil.CsvConfig.builder().useHeader(false).build();
            List<Person> actualPersons = CsvUtil.read(csvContent, Person.class, config);
            assertThat(actualPersons).containsExactlyElementsOf(testPersons);
        }

        @Test
        @DisplayName("Должен возвращать пустой список для пустой CSV строки")
        void shouldReturnEmptyListForEmptyCsvString() {
            List<Person> actualPersons = CsvUtil.read("", Person.class, CsvUtil.CsvConfig.defaults());
            assertThat(actualPersons).isEmpty();
        }

        @Test
        @DisplayName("Должен возвращать пустой список для CSV строки только с заголовком")
        void shouldReturnEmptyListForHeaderOnlyCsvString() {
            String csvContent = "name,age,city,birthDate\n";
            List<Person> actualPersons = CsvUtil.read(csvContent, Person.class, CsvUtil.CsvConfig.defaults());
            assertThat(actualPersons).isEmpty();
        }
    }

    @Nested
    @DisplayName("Интеграционные тесты с файловой системой")
    class FileIntegrationTests {

        // Аннотация JUnit 5, которая создает и автоматически очищает временную директорию для каждого теста.
        @TempDir
        Path tempDir;

        @Test
        @DisplayName("Должен корректно записывать и читать файл (round-trip) с настройками по умолчанию")
        void shouldPerformFileRoundTripWithDefaultConfig() throws IOException {
            Path csvFile = tempDir.resolve("persons_default.csv");
            // 1. Записываем данные в файл
            CsvUtil.write(csvFile, testPersons, Person.class);
            // Проверка содержимого файла (опционально, но полезно для отладки)
            String fileContent = Files.readString(csvFile, StandardCharsets.UTF_8);
            assertThat(fileContent).contains("John Doe,30,\"New York\",1994-05-15");
            assertThat(fileContent).startsWith("name,age,city,birthDate");
            // 2. Читаем данные из того же файла
            List<Person> readPersons = CsvUtil.read(csvFile, Person.class);
            // 3. Сверяем, что прочитанные данные идентичны исходным
            assertThat(readPersons).isEqualTo(testPersons);
        }

        @Test
        @DisplayName("Должен корректно записывать и читать файл (round-trip) с кастомной конфигурацией")
        void shouldPerformFileRoundTripWithCustomConfig() {
            Path csvFile = tempDir.resolve("persons_custom.csv");
            CsvUtil.CsvConfig config = CsvUtil.CsvConfig.builder()
                    .separator(';')
                    .useHeader(false)
                    .charset(StandardCharsets.ISO_8859_1)
                    .build();
            // 1. Записываем с кастомной конфигурацией
            CsvUtil.write(csvFile, testPersons, Person.class, config);
            // 2. Читаем с той же кастомной конфигурацией
            List<Person> readPersons = CsvUtil.read(csvFile, Person.class, config);
            // 3. Проверяем результат
            assertThat(readPersons).isEqualTo(testPersons);
        }
    }

    @Nested
    @DisplayName("Обработка ошибок и граничных случаев")
    class ErrorHandlingTests {
        @Test
        @DisplayName("Должен выбрасывать NullPointerException при передаче null-аргументов в 'toString'")
        void shouldThrowNpeForNullArgumentsInToString() {
            assertThatThrownBy(() -> CsvUtil.toString(null, Person.class))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Список данных для сериализации не может быть null");
            assertThatThrownBy(() -> CsvUtil.toString(testPersons, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Класс данных для сериализации не может быть null");
            assertThatThrownBy(() -> CsvUtil.toString(testPersons, Person.class, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Конфигурация CSV не может быть null");
        }

        @Test
        @DisplayName("Должен выбрасывать NullPointerException при передаче null-аргументов в 'read'")
        void shouldThrowNpeForNullArgumentsInRead() {
            String csvContent = "name,age\nJohn,30";
            CsvUtil.CsvConfig config = CsvUtil.CsvConfig.defaults();
            assertThatThrownBy(() -> CsvUtil.read((String) null, Person.class, config))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Содержимое CSV не может быть null");
            assertThatThrownBy(() -> CsvUtil.read(csvContent, null, config))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Класс для десериализации не может быть null");
            assertThatThrownBy(() -> CsvUtil.read(csvContent, Person.class, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Конфигурация CSV не может быть null");
        }

        @Test
        @DisplayName("Должен выбрасывать UncheckedIOException при чтении несуществующего файла")
        void shouldThrowUncheckedIoExceptionForNonExistentFile() {
            Path nonExistentFile = Path.of("non/existent/file.csv");
            assertThatThrownBy(() -> CsvUtil.read(nonExistentFile, Person.class))
                    .isInstanceOf(UncheckedIOException.class)
                    .hasMessage("Ошибка чтения файла: " + nonExistentFile);
        }

        @Test
        @DisplayName("Должен выбрасывать CsvOperationException при десериализации некорректных данных")
        void shouldThrowCsvOperationExceptionForMalformedCsv() {
            // Строка с неправильным количеством столбцов
            String malformedCsv = """
                    name,age,city,birthDate
                    John Doe,30
                    """;
            assertThatThrownBy(() -> CsvUtil.read(malformedCsv, Person.class, CsvUtil.CsvConfig.defaults()))
                    .isInstanceOf(CsvUtil.CsvOperationException.class)
                    .hasMessage("Ошибка десериализации из CSV")
                    // Проверяем, что исходная причина - ошибка парсинга Jackson
                    .getCause().isInstanceOf(IOException.class);
        }
    }
}
