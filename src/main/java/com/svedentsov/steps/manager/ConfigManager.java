package com.svedentsov.steps.manager;

import com.svedentsov.config.AllureConfig;
import com.svedentsov.config.AppConfig;
import com.svedentsov.config.SystemConfig;
import com.svedentsov.config.TimeoutConfig;
import org.aeonbits.owner.ConfigFactory;

import java.util.Optional;

/**
 * Класс-менеджер для работы с конфигурациями приложения.
 * Предоставляет доступ к различным конфигурациям, таким как конфигурация приложения, системы, таймаутов и Allure.
 */
public class ConfigManager {

    private static AppConfig appConfig;
    private static AllureConfig allureConfig;
    private static SystemConfig systemConfig;
    private static TimeoutConfig timeoutConfig;

    /**
     * Возвращает экземпляр конфигурации приложения.
     *
     * @return экземпляр {@link AppConfig}
     */
    public static AppConfig app() {
        return Optional.ofNullable(appConfig).orElseGet(() -> appConfig = ConfigFactory.create(AppConfig.class));
    }

    /**
     * Возвращает экземпляр системной конфигурации.
     *
     * @return экземпляр {@link SystemConfig}
     */
    public static SystemConfig system() {
        return Optional.ofNullable(systemConfig).orElseGet(() -> systemConfig = ConfigFactory.create(SystemConfig.class));
    }

    /**
     * Возвращает экземпляр конфигурации таймаутов.
     *
     * @return экземпляр {@link TimeoutConfig}
     */
    public static TimeoutConfig timeout() {
        return Optional.ofNullable(timeoutConfig).orElseGet(() -> timeoutConfig = ConfigFactory.create(TimeoutConfig.class));
    }

    /**
     * Возвращает экземпляр конфигурации Allure.
     *
     * @return экземпляр {@link AllureConfig}
     */
    public static AllureConfig allure() {
        return Optional.ofNullable(allureConfig).orElseGet(() -> allureConfig = ConfigFactory.create(AllureConfig.class));
    }
}
