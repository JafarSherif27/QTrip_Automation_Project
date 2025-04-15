package qtriptest.tests;

import qtriptest.DriverSingleton;
// import qtriptest.DP;
import qtriptest.pages.HomePage;
import qtriptest.pages.LoginPage;
import qtriptest.pages.RegisterPage;
import java.net.MalformedURLException;
import org.testng.Assert;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import qtriptest.utils.UtilityMethods;


// @SuppressWarnings("unused")
public class testCase_01 {
    RemoteWebDriver driver;
    RegisterPage registerPage;
    HomePage homePage;
    LoginPage loginPage;
    

    // Iinitialize webdriver for our Unit Tests
    // Test level set-up
    @BeforeTest(alwaysRun = true)
    public void createDriver() throws MalformedURLException {

        this.driver = DriverSingleton.getDriverInstance("chrome");
        UtilityMethods.logStatus("Driver", "Initializing driver", "Success");

        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();

        this.homePage = new HomePage(driver);
        this.registerPage = new RegisterPage(driver);
        this.loginPage = new LoginPage(driver);
    }


    // Test Implementation
    @Test(priority = 1, groups= ("Login Flow"), description = "Verify user registration, login and logout flow", dataProvider = "data-provider", dataProviderClass = qtriptest.DP.class, enabled = true)
    public void TestCase01(String username, String password) {

        try {
            UtilityMethods.logStatus("TestCase01", "Verify user onboarding flow", "started");

            System.out.println("USERNAME FROM DP:: "+ username);
            System.out.println("PASSWORD FROM DP:: "+ password);

            homePage.navigateToHomePage();
            homePage.clickOnRegisterButton();
            Assert.assertTrue(registerPage.isUserNavigatedToRegisterPage(),
                    "Error: Failed to navigate to Register page");
            registerPage.registerNewUser(username, password, password, true);
            Assert.assertTrue(loginPage.isUserNavigatedToLoginPage(),
                    "Error: Failed to navigate to login page");
            loginPage.performLogin(registerPage.lastGeneratedEmail, password);
            Assert.assertTrue(homePage.isUserLoggedIn(), "Error: Failed to login user");
            homePage.logoutUser();
            Assert.assertTrue(homePage.isUserLoggedOut(), "Error: Failed to logout user");

            UtilityMethods.logStatus("TestCase01", "Verify user onboarding flow", "success");

        } catch (AssertionError ae) {
            UtilityMethods.logError("TestCase01", "Verify user onboarding flow", "failed", ae);
            throw ae;
        } catch (Exception e) {
            UtilityMethods.logError("TestCase01", "Verify user onboarding flow", "failed", e);
        }

    }



    // Tear down for driver
    @AfterTest(enabled = true)
    public void quitDriver() throws MalformedURLException {
        driver.quit();
        UtilityMethods.logStatus("Driver", "Quitting driver", "Success");
    }



}
