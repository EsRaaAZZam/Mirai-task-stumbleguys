package com.stumbleguys.tests;

import com.stumbleguys.driver.DriverManager;
import com.stumbleguys.driver.TargetFactory;
import com.stumbleguys.pages.*;
import com.stumbleguys.report.AllureManager;
import com.stumbleguys.report.AllureUtils;
import com.stumbleguys.utils.CustomSoftAssert;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.time.Duration;

import static com.stumbleguys.config.ConfigurationManager.configuration;

public abstract class BaseTest {

    protected LandingPage  landingPage;
    protected LoginPage    loginPage;
    protected StorePage    storePage;
    protected CheckoutPage checkoutPage;
    protected GamePage     gamePage;
    protected CustomSoftAssert softAssert;

    @BeforeClass(alwaysRun = true)
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) {
        WebDriver driver = new TargetFactory().createInstance(browser);
        DriverManager.setDriver(driver);
        DriverManager.getDriver().manage().window().maximize();
        DriverManager.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(configuration().timeout()));
        DriverManager.getDriver().get(configuration().url());
        initPages();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeEachTest() {
        softAssert = new CustomSoftAssert();
    }

    @AfterMethod(alwaysRun = true)
    public void afterEachTest(ITestResult result) {
        AllureUtils.attachScreenshotOnFailure(result);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        DriverManager.quit();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        AllureManager.setEnvironmentInfo();
    }

    protected void initPages() {
        landingPage  = new LandingPage();
        loginPage    = new LoginPage();
        storePage    = new StorePage();
        checkoutPage = new CheckoutPage();
        gamePage     = new GamePage();
    }
}
