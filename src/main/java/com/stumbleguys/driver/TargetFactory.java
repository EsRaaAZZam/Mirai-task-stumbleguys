package com.stumbleguys.driver;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;

import static com.stumbleguys.config.ConfigurationManager.configuration;

public class TargetFactory {

    public WebDriver createInstance(String browser) {
        String target = configuration().target().toUpperCase();
        return switch (target) {
            case "LOCAL" -> BrowserFactory.valueOf(configuration().browser().toUpperCase()).createLocalDriver();
            case "LOCAL_SUITE" -> BrowserFactory.valueOf(browser.toUpperCase()).createLocalDriver();
            case "SELENIUM_GRID" -> createRemoteInstance(BrowserFactory.valueOf(browser.toUpperCase()).getOptions());
            default -> throw new IllegalArgumentException("Unsupported target: " + target);
        };
    }

    private RemoteWebDriver createRemoteInstance(MutableCapabilities capabilities) {
        try {
            String gridUrl = String.format("http://%s:%s", configuration().gridUrl(), configuration().gridPort());
            return new RemoteWebDriver(URI.create(gridUrl).toURL(), capabilities);
        } catch (java.net.MalformedURLException e) {
            throw new RuntimeException("Invalid Selenium Grid URL", e);
        }
    }
}
