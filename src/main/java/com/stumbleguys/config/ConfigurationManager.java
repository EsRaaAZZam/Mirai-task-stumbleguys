package com.stumbleguys.config;

import org.aeonbits.owner.ConfigCache;

public class ConfigurationManager {

    private ConfigurationManager() {
    }

    public static Configuration configuration() {
        String environment = System.getProperty("env");
        if (environment == null || environment.isBlank()) {
            environment = "stumbleguys";
            System.setProperty("env", environment);
        }
        return ConfigCache.getOrCreate(Configuration.class);
    }
}
