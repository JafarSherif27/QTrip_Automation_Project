package qtriptest.tests;

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

public class testCase_01 {
    RemoteWebDriver driver;
    RegisterPage registerPage;
    HomePage homePage;
    LoginPage loginPage;

    private static boolean isLogFileInitialized = false;
    private static int noOfErrorLogs = 1;

    // Method to help us log our Unit Tests
    public void logStatus(String type, String message, String status) {
        System.out.println(String.format("%s |  %s  |  %s | %s",
                String.valueOf(java.time.LocalDateTime.now()), type, message, status));
    }

    // Method to help us log Errors in a text file
    public void logError(String type, String message, String status, Throwable e) {
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

    // Iinitialize webdriver for our Unit Tests
    // Test level set-up
    @BeforeTest(alwaysRun = true)
    public void createDriver() throws MalformedURLException {

        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        // This line creates a new instance of RemoteWebDriver in each test class
        this.driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);


        driver.manage().window().maximize();
        this.homePage = new HomePage(driver);
        this.registerPage = new RegisterPage(driver);
        this.loginPage = new LoginPage(driver);
    }


    // Test Implementation
    @Test(description = "Verify user registration, login and logout flow", dataProvider = "data-provider", dataProviderClass = qtriptest.DP.class, enabled = true)
    public void TestCase01(String username, String password) {

        try {
            logStatus("TestCase01", "Verify user onboarding flow", "started");

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

            logStatus("TestCase01", "Verify user onboarding flow", "success");

        } catch (AssertionError ae) {
            logError("TestCase01", "Verify user onboarding flow", "failed", ae);
            throw ae;
        } catch (Exception e) {
            logError("TestCase01", "Verify user onboarding flow", "failed", e);
        }

    }



    // Tear down for driver
    @AfterTest(enabled = true)
    public void quitDriver() throws MalformedURLException {
        driver.quit();
        logStatus("Driver", "Quitting driver", "Success");
    }



}
