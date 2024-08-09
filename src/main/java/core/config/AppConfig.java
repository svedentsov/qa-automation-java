package core.config;

import org.aeonbits.owner.Config;

import static org.aeonbits.owner.Config.*;

@LoadPolicy(LoadType.MERGE)
@Sources({
        "system:properties",
        "classpath:config/config.properties"
})
public interface AppConfig extends Config {

    @Key("web.url")
    String webUrl();

    @Key("api.url")
    String apiUrl();

    @Key("web.username")
    String userLogin();

    @Key("web.password")
    String userPassword();

    @Key("db.url")
    String dbUrl();

    @Key("db.username")
    String dbUsername();

    @Key("db.password")
    String dbPassword();
}
