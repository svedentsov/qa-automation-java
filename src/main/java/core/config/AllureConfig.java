package core.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources("file:src/main/resources/properties/allure.properties")
public interface AllureConfig extends Config {

    @Key("allure.results.directory")
    String resultsDirectory();
}
