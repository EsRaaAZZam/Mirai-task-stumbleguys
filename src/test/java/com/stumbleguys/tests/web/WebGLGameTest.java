package com.stumbleguys.tests.web;

import com.stumbleguys.driver.DriverManager;
import com.stumbleguys.driver.TargetFactory;
import com.stumbleguys.listeners.RetryAnalyzer;
import com.stumbleguys.tests.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.Duration;

import static com.stumbleguys.config.ConfigurationManager.configuration;

@Feature("WebGL Game")
public class WebGLGameTest extends BaseTest {

    private static final int GAME_LOAD_TIMEOUT_SEC = 120;

    @BeforeClass(alwaysRun = true)
    @Parameters("browser")
    @Override
    public void setUp(@Optional("chrome") String browser) {
        super.setUp(browser);
        landingPage.acceptCookiesIfPresent();
    }

    @BeforeMethod(alwaysRun = true)
    public void ensureBrowserAlive() {
        try {
            DriverManager.getDriver().getCurrentUrl();
        } catch (Exception e) {
            DriverManager.quit();
            WebDriver driver = new TargetFactory().createInstance("chrome");
            DriverManager.setDriver(driver);
            DriverManager.getDriver().manage().window().maximize();
            DriverManager.getDriver().manage().timeouts()
                    .implicitlyWait(Duration.ofSeconds(configuration().timeout()));
            initPages();
        }
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    @Description("TC-GAME-01: Verify the browser supports WebGL before launching the game")
    @Story("WebGL support check")
    public void testWebGLSupportedInBrowser() {
        softAssert.assertTrue(gamePage.isWebGLSupported(),
                "The browser must support WebGL to run the Stumble Guys web game.");
        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, dependsOnMethods = "testWebGLSupportedInBrowser")
    @Description("TC-GAME-02: Verify clicking Play Now navigates to the game and the WebGL canvas loads")
    @Story("Game canvas loads")
    public void testGameCanvasLoadsAfterPlayNow() {
        DriverManager.getDriver().get(configuration().url());
        landingPage.clickPlayNow();

        boolean canvasPresent = gamePage.waitForGameCanvas(30);
        softAssert.assertTrue(canvasPresent,
                "WebGL canvas element should be present in the DOM after navigating to the game URL.");

        if (canvasPresent) {
            System.out.println("[GAME] Canvas dimensions: " + gamePage.getCanvasDimensions());
        }

        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, dependsOnMethods = "testGameCanvasLoadsAfterPlayNow")
    @Description("TC-GAME-03: Verify the game loading screen appears and eventually the game is ready")
    @Story("Game loading progress")
    public void testGameLoadingScreenAppearsAndCompletes() {
        if (!DriverManager.getDriver().getCurrentUrl().contains("/play")) {
            DriverManager.getDriver().get(configuration().url());
            landingPage.clickPlayNow();
        }
        gamePage.waitForGameCanvas(30);

        boolean loadingVisible = gamePage.isLoadingBarVisible();
        System.out.println("[GAME] Loading bar visible: " + loadingVisible);

        boolean gameLoaded = gamePage.waitForGameToLoad(GAME_LOAD_TIMEOUT_SEC);
        softAssert.assertTrue(gameLoaded,
                "Game should finish loading within " + GAME_LOAD_TIMEOUT_SEC + " seconds.");
        softAssert.assertTrue(gamePage.isCanvasPresent(),
                "Canvas must still be present after game load completes.");
        softAssert.assertAll();
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, dependsOnMethods = {"testGameCanvasLoadsAfterPlayNow", "testGameLoadingScreenAppearsAndCompletes"})
    @Description("TC-GAME-04: Verify the user can interact with the game canvas (click to focus/dismiss intro)")
    @Story("Game canvas interaction")
    public void testGameCanvasIsInteractable() {
        if (!DriverManager.getDriver().getCurrentUrl().contains("/play")) {
            DriverManager.getDriver().get(configuration().url());
            landingPage.clickPlayNow();
        }
        softAssert.assertTrue(gamePage.waitForGameCanvas(30), "Canvas must be present to test interaction.");

        gamePage.waitForGameToLoad(GAME_LOAD_TIMEOUT_SEC);
        softAssert.assertTrue(gamePage.waitForCanvasToBeInteractable(30),
                "Canvas container must be visible and game must be initialised before interaction.");

        gamePage.clickCentreOfCanvas();
        System.out.println("[GAME] Clicked centre of canvas.");

        gamePage.clickInsideCanvas(50, 90);
        System.out.println("[GAME] Clicked bottom-centre of canvas (typical 'Play' button area).");

        softAssert.assertTrue(gamePage.isCanvasPresent(),
                "Canvas should remain visible after user interaction.");
        softAssert.assertAll();
    }
}
