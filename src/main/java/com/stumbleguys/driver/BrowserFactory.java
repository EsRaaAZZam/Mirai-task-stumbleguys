package com.stumbleguys.driver;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;

import static com.stumbleguys.config.ConfigurationManager.configuration;

public enum BrowserFactory {

    CHROME {
        @Override
        public WebDriver createLocalDriver() {
            return new ChromeDriver(getOptions());
        }

        @Override
        public ChromeOptions getOptions() {
            var options = new ChromeOptions();
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-infobars");
            options.addArguments("--disable-notifications");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
            if (Boolean.TRUE.equals(configuration().headless())) {
                options.addArguments("--headless=new");
                options.addArguments("--window-size=1920,1080");
            }
            return options;
        }
    },

    FIREFOX {
        @Override
        public WebDriver createLocalDriver() {
            return new FirefoxDriver(getOptions());
        }

        @Override
        public FirefoxOptions getOptions() {
            var options = new FirefoxOptions();
            options.addArguments("-width=1920", "-height=1080");
            if (Boolean.TRUE.equals(configuration().headless())) {
                options.addArguments("--headless");
            }
            return options;
        }
    },

    EDGE {
        @Override
        public WebDriver createLocalDriver() {
            return new EdgeDriver(getOptions());
        }

        @Override
        public EdgeOptions getOptions() {
            var options = new EdgeOptions();
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-notifications");
            options.addArguments("--remote-allow-origins=*");
            if (Boolean.TRUE.equals(configuration().headless())) {
                options.addArguments("--headless=new");
            }
            return options;
        }
    };

    public abstract WebDriver createLocalDriver();

    public abstract AbstractDriverOptions<?> getOptions();
}
