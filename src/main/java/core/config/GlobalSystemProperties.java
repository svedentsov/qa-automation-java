package core.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
public interface GlobalSystemProperties extends Config {

    @DefaultValue("${size}")
    String runSize();
}