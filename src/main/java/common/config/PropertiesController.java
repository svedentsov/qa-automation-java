package common.config;

import org.aeonbits.owner.ConfigFactory;

import java.util.Optional;

public class PropertiesController {

    private static AppTimeoutConfig appTimeoutConfig;
    private static GlobalSystemProperties systemProperties;
    private static AllureConfig allureConfig;
    private static AppConfig appConfig;

    public static AppTimeoutConfig appTimeoutConfig() {
        return Optional.ofNullable(appTimeoutConfig).orElseGet(() -> appTimeoutConfig = ConfigFactory.create(AppTimeoutConfig.class));
    }

    public static GlobalSystemProperties systemProperties() {
        return Optional.ofNullable(systemProperties).orElseGet(() -> systemProperties = ConfigFactory.create(GlobalSystemProperties.class, System.getProperties()));
    }

    public static AllureConfig allureConfig() {
        return Optional.ofNullable(allureConfig).orElseGet(() -> allureConfig = ConfigFactory.create(AllureConfig.class));
    }

    public static AppConfig getAppConfig() {
        return Optional.ofNullable(appConfig).orElseGet(() -> appConfig = ConfigFactory.create(AppConfig.class));
    }
}
