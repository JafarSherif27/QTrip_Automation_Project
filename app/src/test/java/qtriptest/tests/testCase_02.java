package qtriptest.tests;

import qtriptest.DriverSingleton;
import qtriptest.pages.AdventurePage;
// import qtriptest.DP;
import qtriptest.pages.HomePage;
import qtriptest.pages.LoginPage;
import qtriptest.pages.RegisterPage;
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
import qtriptest.utils.UtilityMethods;

@SuppressWarnings("unused")
public class testCase_02 {

    RemoteWebDriver driver;
    SoftAssert softAssert;
    RegisterPage registerPage;
    HomePage homePage;
    LoginPage loginPage;
    AdventurePage adventurePage;


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
        this.softAssert = new SoftAssert();
    }



    // Test Implementation
    @Test(priority = 2, groups= ("Search and Filter flow"), description = "Verify that Search and filters work fine", dataProvider = "data-provider",
            dataProviderClass = qtriptest.DP.class, enabled = true)
    public void TestCase02(String cityName, String categoryToSelect, String filterDuration,
            String expectedFilteredResults, String expectedUnFilteredResults) {

        try {
            UtilityMethods.logStatus("TestCase02", "Verify functionality of Search & Filters",
                    "started");

            System.out.println("CITY NAME FROM DP:: "+ cityName);
            System.out.println("CATEGORY TO SELECT FROM DP:: "+ categoryToSelect);
            System.out.println("DURATION FILTER FROM DP:: "+ filterDuration);
            System.out.println("EXPECTED RESULTS AFTER FILTER FROM DP:: "+
            expectedFilteredResults);
            System.out.println("EXPECTED RESULTS BEFORE FILTER FROM DP:: "+
            expectedUnFilteredResults);


            homePage.navigateToHomePage();
            homePage.searchCity("Coimbatore");
            softAssert.assertTrue(homePage.isNoCityFoundTextDisplayed(),
                    "Error: 'The No city found' message is not displayed");
            homePage.searchCity(cityName);
            softAssert.assertTrue(homePage.assertAutoCompleteText(cityName),
                    "Error: The given text is not present in the auto complete result");
            homePage.selectCity(cityName);
            adventurePage.selectFilterByDuration(filterDuration);
            softAssert.assertTrue(adventurePage.verifySelectedFilter(filterDuration),
                    "Error: The search results is not relevant to the duration filter applied");
            adventurePage.selectCategory(categoryToSelect);
            softAssert.assertTrue(adventurePage.verifySelectedCategory(categoryToSelect),
                    "Error: The search results are not relevant to the category selected");
            softAssert.assertEquals(Integer.parseInt(expectedFilteredResults), adventurePage.getTotalNoOfResults(),
                    "Error: The total number of actual results after applying filter & category does not match the expected number of results");
            adventurePage.clearAllFilterAndCategory();
            softAssert.assertEquals(Integer.parseInt(expectedUnFilteredResults), adventurePage.getTotalNoOfResults(),
                    "Error: The total number of actual results before applying filter & category does not match the expected number of results");


            softAssert.assertAll();

            UtilityMethods.logStatus("TestCase02", "Verify functionality of Search & Filters",
                    "success");

        } catch (AssertionError ae) {
            UtilityMethods.logError("TestCase02", "Verify functionality of Search & Filters",
                    "failed", ae);
            throw ae;
        } catch (Exception e) {
            UtilityMethods.logError("TestCase02", "Verify functionality of Search & Filters",
                    "failed", e);
        }

    }



    // Tear down for driver
    @AfterTest(enabled = true)
    public void quitDriver() throws MalformedURLException {
        driver.quit();
        UtilityMethods.logStatus("Driver", "Quitting driver", "Success");
    }


}


