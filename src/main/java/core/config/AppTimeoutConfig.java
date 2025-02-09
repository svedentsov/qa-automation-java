package core.config;

import core.config.converters.DurationConverter;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

import java.time.Duration;

import static org.aeonbits.owner.Config.*;
import static org.aeonbits.owner.Config.LoadType.MERGE;

@Sources("file:src/main/resources/properties/timeout.properties")
@LoadPolicy(MERGE)
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
