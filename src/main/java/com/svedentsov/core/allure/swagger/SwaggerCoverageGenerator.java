package com.svedentsov.core.allure.swagger;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipException;

/**
 * Генерирует отчет о покрытии Swagger для указанного файла Swagger и конфигурации.
 */
@Slf4j
public class SwaggerCoverageGenerator {

    // URL для загрузки файла Swagger, используемого при генерации отчета
    private static final String SWAGGER_URL = "https://petstore.swagger.io/v2/swagger.json";
    // URL для загрузки архива с инструментом генерации отчета
    private static final String COVERAGE_ZIP_URL = "https://github.com/viclovsky/swagger-coverage/releases/download/1.5.0/swagger-coverage-1.5.0.zip";
    // Путь к директории, в которой будут сохраняться различные файлы, используемые при генерации отчета
    private static final String COVERAGE_DIR = "./coverage/";
    // Путь к файлу Swagger, загруженного для генерации отчета
    private static final String PATH_TO_SWAGGER_JSON = COVERAGE_DIR + "swagger.json";
    // Путь к конфигурационному файлу, используемому при генерации отчета
    private static final String PATH_TO_COVERAGE_CONFIG = COVERAGE_DIR + "swagger-coverage.json";
    // Путь к исполняемому файлу инструмента генерации отчета
    private static final String PATH_TO_COVERAGE_COMMAND = COVERAGE_DIR + "swagger-coverage-commandline-1.5.0/bin/swagger-coverage-commandline.bat";
    // Путь к папке со сгенерированными файлами с информацией о покрытии
    private static final String COVERAGE_OUTPUT_PATH = "./swagger-coverage-output";

    /**
     * Основной метод, который загружает файлы, генерирует отчет и логирует результаты.
     */
    @SneakyThrows
    public static void generate() {
        downloadCoverageZip();
        downloadSwaggerFile();
        generateCoverageReport();
    }

    /**
     * Загружает архив с инструментом генерации отчета о покрытии Swagger и разархивирует его.
     */
    @SneakyThrows
    private static void downloadCoverageZip() {
        log.info("Загрузка инструмента для генерации отчетов о покрытии Swagger");

        // Создание директории для хранения отчетов о покрытии Swagger, если еще не существует
        File coverageDir = new File(COVERAGE_DIR);
        if (!coverageDir.exists() && !coverageDir.mkdirs()) {
            throw new IOException("Не удалось создать директорию " + COVERAGE_DIR);
        }

        // Загрузка архива с инструментом генерации отчета о покрытии
        File coverageZipFile = new File(COVERAGE_DIR + "swagger-coverage-1.5.0.zip");
        try {
            FileUtils.copyURLToFile(new URL(COVERAGE_ZIP_URL), coverageZipFile);
        } catch (IOException e) {
            log.error("Ошибка загрузки инструмента для генерации отчетов о покрытии Swagger", e);
            throw e;
        }

        // Разархивирование архива
        try (ZipFile zipFile = new ZipFile(coverageZipFile)) {
            zipFile.extractAll(COVERAGE_DIR);
        } catch (ZipException e) {
            log.error("Ошибка распаковки инструмента для генерации отчетов о покрытии Swagger", e);
            throw e;
        }

        // Удаление разархивированного архива
        if (!coverageZipFile.delete()) {
            log.warn("Не удалось удалить файл архива {}", coverageZipFile.getAbsolutePath());
        }
        log.info("Инструмент для генерации отчетов о покрытии Swagger успешно загружен");
    }

    /**
     * Скачивает файл Swagger из указанного URL и сохраняет его на диск.
     * Если файл уже существует, то он будет перезаписан.
     */
    @SneakyThrows
    private static void downloadSwaggerFile() {
        log.info("Скачивание файла Swagger из {}", SWAGGER_URL);

        // Создание директории для хранения отчетов о покрытии Swagger, если еще не существует
        File coverageDir = new File(COVERAGE_DIR);
        if (!coverageDir.exists()) {
            coverageDir.mkdir();
        }

        // Загрузка файла Swagger
        RequestSpecification request = RestAssured.given();
        Response response = request.get(SWAGGER_URL);

        // Проверка, что ответ с кодом 200 (OK)
        if (response.getStatusCode() != 200) {
            throw new IOException("Ошибка при загрузке файла Swagger. Код ответа: " + response.getStatusCode());
        }

        // Сохранение файла Swagger на диск
        File swaggerFile = new File(PATH_TO_SWAGGER_JSON);
        FileUtils.writeStringToFile(swaggerFile, response.getBody().asString(), "UTF-8", false);
        log.info("Файл Swagger сохранен в {}", PATH_TO_SWAGGER_JSON);
    }

    /**
     * Генерирует отчет о покрытии Swagger, используя загруженный файл Swagger и конфигурацию.
     */
    @SneakyThrows
    private static void generateCoverageReport() {
        log.info("Начало генерации отчета о покрытии Swagger");

        String[] command = {
                PATH_TO_COVERAGE_COMMAND,
                "-s", PATH_TO_SWAGGER_JSON,
                "-i", COVERAGE_OUTPUT_PATH,
                "-c", PATH_TO_COVERAGE_CONFIG,
        };

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Отчет о покрытии Swagger успешно сгенерирован и сохранен в {}", COVERAGE_OUTPUT_PATH);
            } else {
                throw new IOException("Ошибка при генерации отчета о покрытии Swagger. Код выхода: " + exitCode);
            }
        } catch (IOException e) {
            log.error("Ошибка во время запуска процесса генерации отчета о покрытии Swagger", e);
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Прервано выполнение процесса генерации отчета о покрытии Swagger", e);
            throw e;
        }
        log.info("Генерация отчета о покрытии Swagger завершена");
    }
}
