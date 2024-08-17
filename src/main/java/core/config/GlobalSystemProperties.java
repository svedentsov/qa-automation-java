package core.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;

import static org.aeonbits.owner.Config.LoadType.MERGE;

@LoadPolicy(MERGE)
public interface GlobalSystemProperties extends Config {

    @DefaultValue("${size}")
    String runSize();
}