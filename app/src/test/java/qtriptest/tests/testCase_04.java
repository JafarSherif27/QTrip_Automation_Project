package qtriptest.tests;

import qtriptest.DriverSingleton;
import qtriptest.pages.AdventureDetailsPage;
import qtriptest.pages.AdventurePage;
import qtriptest.pages.HistoryPage;
// import qtriptest.DP;
import qtriptest.pages.HomePage;
import qtriptest.pages.LoginPage;
import qtriptest.pages.RegisterPage;
import qtriptest.utils.UtilityMethods;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.testng.Assert;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@SuppressWarnings("unused")
public class testCase_04 {

    RemoteWebDriver driver;
    RegisterPage registerPage;
    HomePage homePage;
    LoginPage loginPage;
    HistoryPage historyPage;
    AdventureDetailsPage adventureDetailsPage;
    AdventurePage adventurePage;
    UtilityMethods utilityMethods;
    SoftAssert softAssert;


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
        this.historyPage = new HistoryPage(driver);
        this.adventurePage = new AdventurePage(driver);
        this.adventureDetailsPage = new AdventureDetailsPage(driver);
        this.utilityMethods = new UtilityMethods(driver);
        this.softAssert = new SoftAssert();

    }



    // Test Implementation
    @Test(priority = 4, groups = ("Reliability Flow"),
            description = "Verify that Booking history can be viewed",
            dataProvider = "data-provider", dataProviderClass = qtriptest.DP.class, enabled = true)
    public void TestCase04(String email, String password, String dataSet1, String dataSet2,
            String dataSet3) {

        try {
            UtilityMethods.logStatus("TestCase04", "Verify Booking History flow", "started");

            System.out.println("EMAIL FROM DP:: " + email);
            System.out.println("PASSWORD FROM DP:: " + password);
            System.out.println("DATASET 1 FROM DP:: " + dataSet1);
            System.out.println("DATASET 2 FROM DP:: " + dataSet2);
            System.out.println("DATASET 3 FROM DP:: " + dataSet3);

            homePage.navigateToHomePage();
            homePage.clickOnRegisterButton();
            registerPage.registerNewUser(email, password, password, true);

            loginPage.performLogin(registerPage.lastGeneratedEmail, password);
            Assert.assertTrue(homePage.isUserLoggedIn(), "Error: user is not logged in");

            utilityMethods.reserveAdventure(dataSet1);
            utilityMethods.reserveAdventure(dataSet2);
            utilityMethods.reserveAdventure(dataSet3);

            historyPage.navigateToHistoryPage();

            // verify no of reservations shown is 3 for 3 set of datas
            // Check if all the bookings are displayed on the history page
            System.out
                    .println("historyPage.GetReservations():: \n" + historyPage.GetReservations());
            System.out.println("historyPage.GetReservations().Size():: \n"
                    + historyPage.GetReservations().size());
            System.out.println("(historyPage.GetReservations().size() == 3):: \n"
                    + (historyPage.GetReservations().size() == 3));

            softAssert.assertTrue((historyPage.GetReservations().size() == 3),
                    "Error: while verifying if all the bookings are displayed on history page");


            homePage.logoutUser();
            softAssert.assertTrue(homePage.isUserLoggedOut(), "Error: failed to logout user");
            softAssert.assertAll();

            UtilityMethods.logStatus("TestCase04", "Verify Booking History flow", "success");

        } catch (AssertionError ae) {
            UtilityMethods.logError("TestCase04", "Verify Booking History flow", "failed", ae);
            throw ae;
        } catch (Exception e) {
            UtilityMethods.logError("TestCase04", "Verify Booking History flow", "failed", e);
        }

    }



    // Tear down for driver
    @AfterTest(enabled = true)
    public void quitDriver() throws MalformedURLException {
        driver.quit();
        UtilityMethods.logStatus("Driver", "Quitting driver", "Success");
    }

}
