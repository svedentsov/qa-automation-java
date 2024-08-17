package core.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.Sources;

import static org.aeonbits.owner.Config.LoadType.MERGE;

/**
 * Интерфейс для системной конфигурации, который предоставляет доступ
 * к настройкам браузера и удалённого драйвера, загружаемым из различных источников.
 */
@LoadPolicy(MERGE)
@Sources({
        "system:properties",
        "classpath:config/local.properties",
        "classpath:config/remote.properties"})
public interface SystemConfig extends Config {

    /**
     * Имя браузера.
     */
    @Key("browser.name")
    @DefaultValue("chrome")
    String browserName();

    /**
     * Версия браузера.
     */
    @Key("browser.version")
    @DefaultValue("91.0")
    String browserVersion();

    /**
     * Размер окна браузера.
     */
    @Key("browser.size")
    @DefaultValue("1920x1080")
    String browserSize();

    /**
     * URL удалённого драйвера.
     */
    @Key("remote.driver.url")
    String remoteDriverUrl();

    /**
     * Имя пользователя для удалённого драйвера.
     */
    @Key("remote.driver.user")
    String remoteDriverUser();

    /**
     * Пароль для удалённого драйвера.
     */
    @Key("remote.driver.pass")
    String remoteDriverPass();

    /**
     * Путь для хранения видео.
     */
    @Key("video.storage")
    String videoStorage();
}
