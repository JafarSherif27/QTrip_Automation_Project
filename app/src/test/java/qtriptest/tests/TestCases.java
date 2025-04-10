package qtriptest.tests;

import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import qtriptest.pages.LoginPage;
import qtriptest.pages.HomePage;
import qtriptest.pages.RegisterPage;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

// Unit test to check correctness of actions
public class TestCases {
    RemoteWebDriver driver;
    RegisterPage registerPage;
    HomePage homePage;
    LoginPage loginPage;
    
    private static boolean isLogFileInitialized = false;
    private static int noOfErrorLogs = 1;
    private static final String HOMEPAGE_URL = "https://qtripdynamic-qa-frontend.vercel.app";

    // Method to help us log our Unit Tests
    public void logStatus(String type, String message, String status) {
        System.out.println(String.format("%s |  %s  |  %s | %s",
                String.valueOf(java.time.LocalDateTime.now()), type, message, status));
    }

    // Method to help us log Errors in a text file
    public void logError(String type, String message, String status, Throwable e) {
        try {
            if (!isLogFileInitialized) {
                File logDir = new File("src/test/java/qtriptest/tests/unitTestErrorLogs");

                if (!logDir.exists()) {
                    logDir.mkdirs();
                }

                File errorLogFile =
                        new File("src/test/java/qtriptest/tests/unitTestErrorLogs/error_log.txt");
                PrintStream errorStream = new PrintStream(errorLogFile);
                System.setErr(errorStream);

                isLogFileInitialized = true;
            }

            logStatus(type, message, status);
            System.err.println("Error log #" + noOfErrorLogs);
            System.err.println(String.format("%s |  %s  |  %s | %s",
                    String.valueOf(java.time.LocalDateTime.now()), type, message, status));
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
    // Class level set-up
    @BeforeClass(alwaysRun = true, enabled = false)
    public void createDriver() throws MalformedURLException {
        logStatus("Driver", "Initializing driver", "Started");
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        this.driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        logStatus("Driver", "Initializing driver", "Success");
        driver.manage().window().maximize();

        this.homePage = new HomePage(driver);
        this.registerPage = new RegisterPage(driver);
        this.loginPage = new LoginPage(driver);
    }


    // This method runs for all methods except for the method with name
    // testHomePage_navigateToHomePage
    // Method level set-up
    @BeforeMethod(enabled = false)
    public void navigateToLandingPage(Method method) {
        if (!method.getName().equals("testHomePage_navigateToHomePage")) {
            logStatus("Landing page", "Navigating to landing page", "Started");
            homePage.navigateToHomePage();
            Assert.assertTrue(driver.getCurrentUrl().contains(HOMEPAGE_URL),
                    "Error: Mismatch in URL of Home page");

            logStatus("Landing page", "Navigating to landing page", "Success");

        }

    }


    // Unit Tests
    @Test(description = "Verify functionality of - navigate to Home page", enabled = false)
    public void testHomePage_navigateToHomePage() {
        logStatus("Unit test", "navigation to Home page", "started");
        try {
            homePage.navigateToHomePage();
            Assert.assertTrue(driver.getCurrentUrl().contains(HOMEPAGE_URL),
                    "Home page URL is not correct");

            logStatus("Unit test", "navigation to Home page", "success");


        } catch (AssertionError ae) {
            logError("Unit test", "navigation to Home page", "failed", ae);
            throw ae; // Re-throw to mark test as failed
        } catch (Exception e) {
            logError("Unit test", "navigation to Home page", "failed", e);

        }
    }


    @Test(description = "Verify functionality of - clicking on Register link", enabled = false)
    public void testHomePage_clickOnRegister() {
        logStatus("Unit test", "clicking on Register link", "started");
        try {

            homePage.clickOnRegisterButton();
            Assert.assertTrue(registerPage.isUserNavigatedToRegisterPage(),
                    "Error: Failed to navigate to register page");

            driver.navigate().back();

            logStatus("Unit test", "clicking on Register link", "success");

        } catch (AssertionError ae) {
            logError("Unit test", "clicking on Register link", "failed", ae);
            throw ae; // Re-throw to mark test as failed
        } catch (Exception e) {
            logError("Unit test", "clicking on Register link", "failed", e);

        }
    }


    @Test(description = "Verify functionality of - checking if user is logged in", enabled = false)
    public void testHomePage_verifyUserLoggedIn() {
        logStatus("Unit test", "Verify if user is logged in", "started");

        try {

            homePage.clickOnRegisterButton();
            Assert.assertTrue(registerPage.isUserNavigatedToRegisterPage(),
                    "Error: Failed to navigate to register page");

            registerPage.registerNewUser("12121@gmail.com", "admin121", "admin121", true);
            loginPage.performLogin(registerPage.lastGeneratedEmail, "admin121");

            Assert.assertTrue(homePage.isUserLoggedIn(), "Error: User is not logged in");

            homePage.logoutUser();

            logStatus("Unit test", "Verify if user is logged in", "success");

        } catch (AssertionError ae) {
            logError("Unit test", "Verify if user is logged in", "failed", ae);
            throw ae; // Re-throw to mark test as failed
         } catch (Exception e) {
            logError("Unit test", "Verify if user is logged in", "failed", e);

        }
    }

    @Test(description = "Verify functionality of - checking if user is logged out", enabled = false)
    public void testHomePage_verifyUserLoggedOut() {
        logStatus("Unit test", "Verify if user is logged out", "started");

        try {

            homePage.clickOnRegisterButton();
            Assert.assertTrue(registerPage.isUserNavigatedToRegisterPage(),
                    "Error: Failed to navigate to register page");

            registerPage.registerNewUser("12121@gmail.com", "admin121", "admin121", true);
            loginPage.performLogin(registerPage.lastGeneratedEmail, "admin121");

            Assert.assertTrue(homePage.isUserLoggedIn(), "Error: User is not logged in");

            homePage.logoutUser();
            Assert.assertTrue(homePage.isUserLoggedOut(), "Error: User is not logged out");

            logStatus("Unit test", "Verify if user is logged out", "success");

        } catch (AssertionError ae) {
            logError("Unit test", "Verify if user is logged out", "failed", ae);
            throw ae; // Re-throw to mark test as failed
         } catch (Exception e) {
            logError("Unit test", "Verify if user is logged out", "failed", e);

        }
    }



    @Test(description = "Verify functionality of - navigate to Resgistration page", enabled = false)
    public void testRegister_navigateToRegisterPage() {
        logStatus("Unit test", "navigation to Register page", "started");
        try {
            homePage.clickOnRegisterButton();
            Assert.assertTrue(registerPage.isUserNavigatedToRegisterPage(),
                    "Error: Failed to navigate to register page");

            driver.navigate().back();

            logStatus("Unit test", "navigation to Register page", "success");

        } catch (AssertionError ae) {
            logError("Unit test", "navigation to Register page", "failed", ae);
            throw ae; // Re-throw to mark test as failed
         }  catch (Exception e) {
            logError("Unit test", "navigation to Register page", "failed", e);

        }
    }


    @Test(description = "Verify functionality of - register new user", enabled = false)
    public void testRegister_registerNewUser() {
        logStatus("Unit test", "register new user", "started");
        try {
            homePage.clickOnRegisterButton();
            Assert.assertTrue(registerPage.isUserNavigatedToRegisterPage(),
                    "Error: Failed to navigate to register page");

            registerPage.registerNewUser("12121@gmail.com", "admin121", "admin121", true);
            // AUT will redirect to login page once registration is successful
            Assert.assertTrue(loginPage.isUserNavigatedToLoginPage(),
                    "Error: User is not registered");

            homePage.navigateToHomePage();

            logStatus("Unit test", "register new user", "success");

        } catch (AssertionError ae) {
            logError("Unit test", "register new user", "failed", ae);
            throw ae; // Re-throw to mark test as failed
         }  catch (Exception e) {
            logError("Unit test", "register new user", "failed", e);

        }
    }



    @Test(description = "Verify functionality of - navigate to Login page", enabled = false)
    public void testLogin_navigateToLoginPage() {
        logStatus("Unit test", "navigation to Login page", "started");
        try {
            homePage.clickOnLoginButton();
            Assert.assertTrue(loginPage.isUserNavigatedToLoginPage(),
                    "Error: Failed to navigate to Login page");

            driver.navigate().back();
            logStatus("Unit test", "navigation to Login page", "success");

        } catch (AssertionError ae) {
            logError("Unit test", "navigation to Login page", "failed", ae);
            throw ae; // Re-throw to mark test as failed
         } catch (Exception e) {
            logError("Unit test", "navigation to Login page", "failed", e);
        }
    }

    @Test(description = "Verify functionality of - perform login", enabled = false)
    public void testLogin_performLogin() {
        logStatus("Unit test", "perform login", "started");
        try {

            homePage.clickOnRegisterButton();
            Assert.assertTrue(registerPage.isUserNavigatedToRegisterPage(),
                    "Error: Failed to navigate to register page");

            registerPage.registerNewUser("12121@gmail.com", "admin121", "admin121", true);
            loginPage.performLogin(registerPage.lastGeneratedEmail, "admin121");

            Assert.assertTrue(homePage.isUserLoggedIn(), "Error: User is not logged in");

            homePage.logoutUser();

            logStatus("Unit test", "perform login", "success");

        } catch (AssertionError ae) {
            logError("Unit test", "perform login", "failed", ae);
            throw ae; // Re-throw to mark test as failed
         }  catch (Exception e) {
            logError("Unit test", "perform login", "failed", e);

        }
    }



    // Quit webdriver after Unit Tests
    @AfterClass(enabled = false)
    public void quitDriver() throws MalformedURLException {
        driver.quit();
        logStatus("Driver", "Quitting driver", "Success");
    }
}
