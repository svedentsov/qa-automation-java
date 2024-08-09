package core.config;

import org.aeonbits.owner.ConfigFactory;

import java.util.Optional;

public class ConfigManager {

    private static AppConfig appConfig;
    private static AllureConfig allureConfig;
    private static SystemConfig systemConfig;
    private static TimeoutConfig timeoutConfig;

    public static AppConfig app() {
        return Optional.ofNullable(appConfig).
                orElseGet(() -> appConfig = ConfigFactory.create(AppConfig.class));
    }

    public static SystemConfig system() {
        return Optional.ofNullable(systemConfig).
                orElseGet(() -> systemConfig = ConfigFactory.create(SystemConfig.class));
    }

    public static TimeoutConfig timeout() {
        return Optional.ofNullable(timeoutConfig).
                orElseGet(() -> timeoutConfig = ConfigFactory.create(TimeoutConfig.class));
    }

    public static AllureConfig allure() {
        return Optional.ofNullable(allureConfig).
                orElseGet(() -> allureConfig = ConfigFactory.create(AllureConfig.class));
    }
}
