package com.stumbleguys.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;

@LoadPolicy(LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:${env}.properties"})
public interface Configuration extends Config {

    @Key("target")
    String target();

    @Key("browser")
    String browser();

    @Key("headless")
    Boolean headless();

    @Key("timeout")
    int timeout();

    @Key("url")
    String url();

    @Key("store.url")
    String storeUrl();

    @Key("game.url")
    String gameUrl();

    @Key("retryCount")
    int retryCount();

    @Key("android.deviceName")
    String androidDeviceName();

    @Key("android.udid")
    String androidUdid();

    @Key("android.platformVersion")
    String androidPlatformVersion();

    @Key("appium.serverUrl")
    String appiumServerUrl();

    @Key("grid.url")
    String gridUrl();

    @Key("grid.port")
    String gridPort();
}
