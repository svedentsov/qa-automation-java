package common.config;

import org.aeonbits.owner.ConfigFactory;

import java.util.Optional;

public class Project {

    public static ProjectConfig config = ConfigFactory.create(ProjectConfig.class, System.getProperties());

    public static boolean isRemoteWebDriver() {
        Optional<String> optionalS = Optional.ofNullable(config.remoteDriverUrl());
        return optionalS.isPresent();
    }

    public static boolean isVideoOn() {
        Optional<String> optionalS = Optional.ofNullable(config.videoStorage());
        return optionalS.isPresent();
    }
}
