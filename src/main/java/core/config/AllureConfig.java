package core.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

/**
 * Интерфейс для конфигурации Allure, используемый для доступа к параметрам, загружаемым из файла properties.
 */
@Sources("file:src/main/resources/properties/allure.properties")
public interface AllureConfig extends Config {

    /**
     * Директория для хранения результатов Allure.
     */
    @Key("allure.results.directory")
    String resultsDirectory();
}
