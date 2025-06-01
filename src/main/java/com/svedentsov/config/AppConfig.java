package com.svedentsov.config;

import org.aeonbits.owner.Config;

import static org.aeonbits.owner.Config.LoadPolicy;
import static org.aeonbits.owner.Config.LoadType.MERGE;
import static org.aeonbits.owner.Config.Sources;

/**
 * Интерфейс для конфигурации приложения, используемый для доступа к параметрам,
 * загружаемым из системных свойств и файла конфигурации.
 */
@LoadPolicy(MERGE)
@Sources({
        "system:properties",
        "classpath:config/config.properties"})
public interface AppConfig extends Config {

    /**
     * URL веб-приложения.
     */
    @Key("web.url")
    String webUrl();

    /**
     * URL API.
     */
    @Key("api.url")
    String apiUrl();

    /**
     * Имя пользователя для веб-приложения.
     */
    @Key("web.username")
    String userLogin();

    /**
     * Пароль пользователя для веб-приложения.
     */
    @Key("web.password")
    String userPassword();

    /**
     * URL базы данных.
     */
    @Key("db.url")
    String dbUrl();

    /**
     * Имя пользователя для базы данных.
     */
    @Key("db.username")
    String dbUsername();

    /**
     * Пароль пользователя для базы данных.
     */
    @Key("db.password")
    String dbPassword();
}
