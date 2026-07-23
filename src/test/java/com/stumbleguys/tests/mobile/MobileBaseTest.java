package com.stumbleguys.tests.mobile;

import com.stumbleguys.mobile.AppiumServerManager;
import com.stumbleguys.mobile.MobileDriverManager;

import static com.stumbleguys.config.ConfigurationManager.configuration;
import com.stumbleguys.pages.*;
import com.stumbleguys.report.AllureManager;
import com.stumbleguys.report.AllureUtils;
import com.stumbleguys.utils.CustomSoftAssert;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;


public abstract class MobileBaseTest {

    protected LandingPage  landingPage;
    protected LoginPage    loginPage;
    protected StorePage    storePage;
    protected CheckoutPage checkoutPage;
    protected CustomSoftAssert softAssert;

    private WebDriver driver() {
        return MobileDriverManager.getDriver();
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        AppiumServerManager.ensureServerRunning();
        MobileDriverManager.createAndroidChromeDriver();
        driver().get(configuration().url());
        initPages();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeEachTest() {
        softAssert = new CustomSoftAssert();
    }

    @AfterMethod(alwaysRun = true)
    public void afterEachTest(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE && MobileDriverManager.getDriver() != null) {
            AllureUtils.attachScreenshot("Failure - " + result.getMethod().getMethodName());
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        MobileDriverManager.quit();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        AllureManager.setMobileEnvironmentInfo();
    }

    protected void initPages() {
        landingPage  = new LandingPage();
        loginPage    = new LoginPage();
        storePage    = new StorePage();
        checkoutPage = new CheckoutPage();
    }

    protected void navigateTo(String url) {
        driver().get(url);
        initPages();
    }
}
