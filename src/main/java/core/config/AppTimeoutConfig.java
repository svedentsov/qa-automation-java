package core.config;

import core.utils.DurationConverter;
import org.aeonbits.owner.Config;

import java.time.Duration;

@Config.Sources("file:src/main/resources/properties/timeout.properties")
@Config.LoadPolicy(Config.LoadType.MERGE)
public interface AppTimeoutConfig extends Config {

    @Key("webdriver.wait.timeout")
    @ConverterClass(DurationConverter.class)
    Duration webdriverWaitTimeout();

    @Key("wait.utils.timeout")
    @ConverterClass(DurationConverter.class)
    Duration utilWaitTimeout();

    @Key("wait.utils.timeout.medium")
    @ConverterClass(DurationConverter.class)
    Duration utilWaitMediumTimeout();

    @Key("wait.utils.timeout.long")
    @ConverterClass(DurationConverter.class)
    Duration utilWaitLongTimeout();
}