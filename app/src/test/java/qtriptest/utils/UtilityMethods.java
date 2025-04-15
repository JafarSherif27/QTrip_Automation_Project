package qtriptest.utils;

import qtriptest.pages.AdventureDetailsPage;
import qtriptest.pages.AdventurePage;
import qtriptest.pages.HistoryPage;
import qtriptest.pages.HomePage;
import qtriptest.pages.LoginPage;
import qtriptest.pages.RegisterPage;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

public class UtilityMethods {


    private static boolean isLogFileInitialized = false;
    private static int noOfErrorLogs = 1;

    RemoteWebDriver driver;
    WebDriverWait wait;
    private boolean status;
    Actions actions;
    JavascriptExecutor js;
    RegisterPage registerPage;
    HomePage homePage;
    LoginPage loginPage;
    HistoryPage historyPage;
    AdventureDetailsPage adventureDetailsPage;
    AdventurePage adventurePage;
    SoftAssert softAssert;
    private static final int MAX_WAIT_TIME = 10;



    // Constructor
    public UtilityMethods(RemoteWebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, MAX_WAIT_TIME);
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;

        homePage = new HomePage(driver);
        registerPage = new RegisterPage(driver);
        loginPage = new LoginPage(driver);
        historyPage = new HistoryPage(driver);
        adventurePage = new AdventurePage(driver);
        adventureDetailsPage = new AdventureDetailsPage(driver);
        softAssert = new SoftAssert();
    }

    // Method to help us log our Unit Tests
    public static void logStatus(String type, String message, String status) {
        System.out.println(String.format("%s |  %s  |  %s | %s",
                String.valueOf(java.time.LocalDateTime.now()), type, message, status));
    }

    // Method to help us log Errors in a text file
    public static void logError(String type, String message, String status, Throwable e) {
        try {
            if (!isLogFileInitialized) {
                File logDir = new File("src/test/java/qtriptest/tests/testErrorLogs");

                if (!logDir.exists()) {
                    logDir.mkdirs();
                }

                File errorLogFile =
                        new File("src/test/java/qtriptest/tests/testErrorLogs/error_log.txt");
                PrintStream errorStream = new PrintStream(errorLogFile);
                System.setErr(errorStream);

                isLogFileInitialized = true;
            }

            logStatus(type, message, status);
            System.err.println("Error log #" + noOfErrorLogs);
            System.err.println(String.format("%s |  %s  |  %s | %s",
                    String.valueOf(java.time.LocalDateTime.now()), type, message, status));
            System.err.println();

            // Print full stack trace to error_log.txt
            if (e != null) {
                e.printStackTrace(System.err);
                System.err.println();
            }

            // Print stackTrace to the terminal
            e.printStackTrace(new PrintStream(new FileOutputStream(FileDescriptor.err)));
            noOfErrorLogs++;

        } catch (Exception ex) {
            ex.printStackTrace();
            logStatus("Logging Error", "Error: while logging error to error_log.txt file ",
                    ex.getMessage());
        }

    }



    // For testCase04
    public void reserveAdventure(String dataSetForReservation) {
        try {
            homePage.navigateToHomePage();

            String[] dataSet = dataSetForReservation.split(";");
            String cityName = dataSet[0];
            String adventureToSelect = dataSet[1];
            String guestName = dataSet[2];
            String date = dataSet[3];
            String noOfPeople = dataSet[4];

            System.out.println();
            System.out.println("====================================================>");
            System.out.println("Inside utility method...");
            System.out.println("dataSet: "+dataSet);
            System.out.println("cityName: "+cityName);
            System.out.println("adventureToSelect: "+adventureToSelect);
            System.out.println("guestName: "+guestName);
            System.out.println("date: "+date);
            System.out.println("noOfPeople: "+noOfPeople);
            System.out.println("====================================================>");
            System.out.println();

            homePage.searchCity(cityName);
            homePage.selectCity(cityName);
            adventurePage.selectAdventure(adventureToSelect);
            adventureDetailsPage.BookAdventure(guestName, date, noOfPeople);
            softAssert.assertTrue(adventureDetailsPage.isBookingSuccessful(),
                    "Error: Failed to verify the booking of adventure");
            

        } catch (Exception e) {
            logError("UtilityMethods", "Error: failed to book adventure for the given dataSet",
                    "failed", e);

        }

    }



}
