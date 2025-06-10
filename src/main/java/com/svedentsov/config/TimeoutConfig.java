package com.svedentsov.config;

import com.svedentsov.config.converter.DurationConverter;
import org.aeonbits.owner.Config;

import java.time.Duration;

import static org.aeonbits.owner.Config.*;
import static org.aeonbits.owner.Config.LoadType.MERGE;

/**
 * Интерфейс для конфигурации таймаутов, предоставляющий доступ
 * к настройкам времени ожидания, загружаемым из файла свойств.
 */
@LoadPolicy(MERGE)
@Sources("file:src/main/resources/properties/timeout.properties")
public interface TimeoutConfig extends Config {

    /**
     * Время ожидания для WebDriver.
     */
    @Key("webdriver.wait.timeout")
    @ConverterClass(DurationConverter.class)
    Duration webdriverWaitTimeout();

    /**
     * Время ожидания для утилит ожидания.
     */
    @Key("wait.utils.timeout")
    @ConverterClass(DurationConverter.class)
    Duration utilWaitTimeout();

    /**
     * Время ожидания для средних утилит ожидания.
     */
    @Key("wait.utils.timeout.medium")
    @ConverterClass(DurationConverter.class)
    Duration utilWaitMediumTimeout();

    /**
     * Время ожидания для длинных утилит ожидания.
     */
    @Key("wait.utils.timeout.long")
    @ConverterClass(DurationConverter.class)
    Duration utilWaitLongTimeout();
}
