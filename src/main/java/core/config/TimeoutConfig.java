package core.config;

import org.aeonbits.owner.Config;
import core.utils.DurationConverter;

import java.time.Duration;

import static org.aeonbits.owner.Config.*;

@LoadPolicy(LoadType.MERGE)
@Sources("file:src/main/resources/properties/timeout.properties")
public interface TimeoutConfig extends Config {

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
