package com.stumbleguys.report;

import com.github.automatedowl.tools.AllureEnvironmentWriter;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;

import static com.stumbleguys.config.ConfigurationManager.configuration;

public class AllureManager {

    private AllureManager() {
    }

    public static void setEnvironmentInfo() {
        var info = new HashMap<String, String>();
        info.put("URL",        configuration().url());
        info.put("Browser",    configuration().browser());
        info.put("Target",     configuration().target());
        info.put("Headless",   String.valueOf(configuration().headless()));
        info.put("Timeout",    String.valueOf(configuration().timeout()));
        AllureEnvironmentWriter.allureEnvironmentWriter(ImmutableMap.copyOf(info));
    }

    public static void setMobileEnvironmentInfo() {
        var info = new HashMap<String, String>();
        info.put("URL",              configuration().url());
        info.put("Platform",         "Android Chrome");
        info.put("Device",           configuration().androidDeviceName());
        info.put("Platform Version", configuration().androidPlatformVersion());
        info.put("Appium Server",    configuration().appiumServerUrl());
        AllureEnvironmentWriter.allureEnvironmentWriter(ImmutableMap.copyOf(info));
    }
}
