package com.svedentsov.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.Sources;

import static org.aeonbits.owner.Config.LoadType.MERGE;

@LoadPolicy(MERGE)
@Sources({
        "system:properties",
        "classpath:config/local.properties",
        "classpath:config/remote.properties"})
public interface ProjectConfig extends Config {

    @Key("browser.name")
    @DefaultValue("chrome")
    String browserName();

    @Key("browser.version")
    @DefaultValue("91.0")
    String browserVersion();

    @Key("browser.size")
    @DefaultValue("1920x1080")
    String browserSize();

    @Key("remote.driver.url")
    String remoteDriverUrl();

    @Key("remote.driver.user")
    String remoteDriverUser();

    @Key("remote.driver.pass")
    String remoteDriverPass();

    @Key("video.storage")
    String videoStorage();
}
