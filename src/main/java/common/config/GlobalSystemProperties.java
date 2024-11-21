package common.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;

@LoadPolicy(Config.LoadType.MERGE)
public interface GlobalSystemProperties extends Config {

    @DefaultValue("${size}")
    String runSize();
}
