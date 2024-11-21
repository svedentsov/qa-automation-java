package common.allure.swagger;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipException;

/**
 * Генератор отчета о покрытии Swagger.
 * Этот класс обеспечивает скачивание, распаковку и использование инструмента swagger-coverage
 * для генерации отчета о покрытии на основе указанного Swagger файла и конфигурации.
 */
@Slf4j
public class SwaggerCoverageGeneratorNew {

    private static final String SWAGGER_URL = "https://petstore.swagger.io/v2/swagger.json";
    private static final String COVERAGE_ZIP_URL = "https://github.com/viclovsky/swagger-coverage/releases/download/1.5.0/swagger-coverage-1.5.0.zip";
    private static final String COVERAGE_DIR = "./coverage/";
    private static final String PATH_TO_SWAGGER_JSON = COVERAGE_DIR + "swagger.json";
    private static final String PATH_TO_COVERAGE_CONFIG = COVERAGE_DIR + "swagger-coverage.json";
    private static final String PATH_TO_COVERAGE_COMMAND = COVERAGE_DIR + "swagger-coverage-commandline-1.5.0/bin/swagger-coverage-commandline.bat";
    private static final String COVERAGE_OUTPUT_PATH = "./swagger-coverage-output";

    /**
     * Генерирует отчет о покрытии Swagger. Метод выполняет следующие шаги:
     * <ul>
     * <li>Проверяет наличие директории для хранения файлов отчета и создает её при необходимости.</li>
     * <li>Скачивает и распаковывает инструмент swagger-coverage.</li>
     * <li>Скачивает Swagger файл.</li>
     * <li>Генерирует отчет о покрытии с использованием скачанных файлов.</li>
     * </ul>
     */
    public static void generate() {
        try {
            ensureDirectoryExists(COVERAGE_DIR);
            downloadAndExtractZip(COVERAGE_ZIP_URL, COVERAGE_DIR + "swagger-coverage-1.5.0.zip", COVERAGE_DIR);
            downloadSwaggerFile(SWAGGER_URL, PATH_TO_SWAGGER_JSON);
            generateCoverageReport();
        } catch (IOException | InterruptedException e) {
            log.error("Ошибка при генерации отчета о покрытии Swagger", e);
        }
    }

    /**
     * Проверяет наличие директории и создает её при отсутствии.
     *
     * @param directoryPath путь к директории
     * @throws IOException если не удалось создать директорию
     */
    private static void ensureDirectoryExists(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Не удалось создать директорию " + directoryPath);
        }
    }

    /**
     * Скачивает ZIP-файл и распаковывает его.
     *
     * @param fileUrl       URL ZIP-файла для скачивания
     * @param outputZipPath путь для сохранения скачанного ZIP-файла
     * @param extractTo     директория для распаковки содержимого ZIP-файла
     * @throws IOException если произошла ошибка при скачивании или распаковке файла
     */
    private static void downloadAndExtractZip(String fileUrl, String outputZipPath, String extractTo) throws IOException {
        log.info("Загрузка и распаковка файла из {}", fileUrl);

        File zipFile = new File(outputZipPath);
        try {
            FileUtils.copyURLToFile(new URL(fileUrl), zipFile);
            try (ZipFile zip = new ZipFile(zipFile)) {
                zip.extractAll(extractTo);
            } catch (ZipException e) {
                log.error("Ошибка распаковки файла {}", outputZipPath, e);
                throw e;
            }

            if (!zipFile.delete()) {
                log.warn("Не удалось удалить файл архива {}", zipFile.getAbsolutePath());
            }
            log.info("Файл успешно загружен и распакован");
        } catch (IOException e) {
            log.error("Ошибка загрузки файла {}", fileUrl, e);
            throw e;
        }
    }

    /**
     * Скачивает Swagger файл по-указанному URL и сохраняет его на диск.
     *
     * @param swaggerUrl URL Swagger файла для скачивания
     * @param outputPath путь для сохранения скачанного Swagger файла
     * @throws IOException если произошла ошибка при скачивании или сохранении файла
     */
    private static void downloadSwaggerFile(String swaggerUrl, String outputPath) throws IOException {
        log.info("Скачивание файла Swagger из {}", swaggerUrl);

        RequestSpecification request = RestAssured.given();
        Response response = request.get(swaggerUrl);

        if (response.getStatusCode() != 200) {
            throw new IOException("Ошибка при загрузке файла Swagger. Код ответа: " + response.getStatusCode());
        }

        try {
            File swaggerFile = new File(outputPath);
            FileUtils.writeStringToFile(swaggerFile, response.getBody().asString(), "UTF-8", false);
            log.info("Файл Swagger сохранен в {}", outputPath);
        } catch (IOException e) {
            log.error("Ошибка сохранения файла Swagger на диск", e);
            throw e;
        }
    }

    /**
     * Генерирует отчет о покрытии Swagger с использованием скачанных файлов и конфигурации.
     *
     * @throws IOException          если произошла ошибка при выполнении команды генерации отчета
     * @throws InterruptedException если процесс генерации отчета был прерван
     */
    private static void generateCoverageReport() throws IOException, InterruptedException {
        log.info("Начало генерации отчета о покрытии Swagger");

        String[] command = {
                PATH_TO_COVERAGE_COMMAND,
                "-s", PATH_TO_SWAGGER_JSON,
                "-i", COVERAGE_OUTPUT_PATH,
                "-c", PATH_TO_COVERAGE_CONFIG,
        };

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
        log.info("Генерация отчета о покрытии Swagger завершена");
    }
}
