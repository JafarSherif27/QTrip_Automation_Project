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
import org.testng.Assert.ThrowingRunnable;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@SuppressWarnings("unused")
public class testCase_03 {

    RemoteWebDriver driver;
    SoftAssert softAssert;
    RegisterPage registerPage;
    HomePage homePage;
    LoginPage loginPage;
    AdventurePage adventurePage;
    AdventureDetailsPage adventureDetailsPage;
    HistoryPage historyPage;

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
        this.adventurePage = new AdventurePage(driver);
        this.adventureDetailsPage = new AdventureDetailsPage(driver);
        this.historyPage = new HistoryPage(driver);
        this.softAssert = new SoftAssert();
    }


    // Test Implementation
    @Test(priority = 3, groups = ("Booking and Cancellation Flow"),
            description = "Verify that adventure booking and cancellation works fine",
            dataProvider = "data-provider", dataProviderClass = qtriptest.DP.class, enabled = true)
    public void TestCase03(String email, String password, String city, String advantureName,
            String guestName, String date, String noOfPeople) {

        try {
            UtilityMethods.logStatus("TestCase03", "Verify Booking and Cancellation Flow",
                    "started");

            System.out.println("Email FROM DP:: " + email);
            System.out.println("PASSWORD FROM DP:: " + password);
            System.out.println("CITY FROM DP:: " + city);
            System.out.println("ADVANTURE FROM DP:: " + advantureName);
            System.out.println("GUEST NAME FROM DP:: " + guestName);
            System.out.println("DATE FROM DP:: " + date);
            System.out.println("NO OF PEOPLE FROM DP:: " + noOfPeople);

            homePage.navigateToHomePage();
            homePage.clickOnRegisterButton();
            registerPage.registerNewUser(email, password, password, true);
            loginPage.performLogin(registerPage.lastGeneratedEmail, password);
            homePage.searchCity(city);
            homePage.selectCity(city);
            adventurePage.selectAdventure(advantureName);
            adventureDetailsPage.BookAdventure(guestName, date, noOfPeople);
            softAssert.assertTrue(adventureDetailsPage.isBookingSuccessful(),
                    "Error: Failed to verify the booking of adventure");
            historyPage.navigateToHistoryPage();

            System.out.println("GetReservations: \n" + historyPage.GetReservations());
            String transactionId = historyPage.getLastGeneratedTransactionId();
            System.out.println("The last generated transaction ID is: " + transactionId);

            if (transactionId != null) {
                historyPage.CancelReservation(transactionId);
                // driver.navigate().refresh();
                softAssert.assertTrue(historyPage.isCancellationSuccess(transactionId),
                        "Error: could'nt verify is the reservation is cancelled for transactionId '"
                                + transactionId + "'");
            }
            homePage.logoutUser();
            softAssert.assertAll();

            UtilityMethods.logStatus("TestCase03", "Verify Booking and Cancellation Flow",
                    "success");

        } catch (AssertionError ae) {
            UtilityMethods.logError("TestCase03", "Verify Booking and Cancellation Flow", "failed",
                    ae);
            throw ae;
        } catch (Exception e) {
            UtilityMethods.logError("TestCase03", "Verify Booking and Cancellation Flow", "failed",
                    e);
        }

    }



    // Tear down for driver
    @AfterTest(enabled = true)
    public void quitDriver() throws MalformedURLException {
        driver.quit();
        UtilityMethods.logStatus("Driver", "Quitting driver", "Success");
    }

}


