package qtriptest.pages;

import qtriptest.utils.UtilityMethods;
// import java.util.concurrent.TimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@SuppressWarnings("unused")
public class HomePage {
    RemoteWebDriver driver;
    WebDriverWait wait;
    private boolean status;
    Actions actions;
    JavascriptExecutor js;
    private AjaxElementLocatorFactory ajax;
    private static final int MAX_WAIT_TIME = 10;
    private static final String HOMEPAGE_URL = "https://qtripdynamic-qa-frontend.vercel.app";



    // Locators
    @FindBy(xpath = "//a[contains(text(), 'Register')]")
    private WebElement registerButton;

    @FindBy(xpath = "//div[normalize-space(text())='Logout']")
    private WebElement logoutButton;

    @FindBy(xpath = "//a[normalize-space(text())='Login Here']")
    private WebElement loginHereButton;

    @FindBy(className = "hero-input")
    private WebElement searchBox;

    // search results in the auto suggestion after enter cityname
    @FindBy(xpath = "//*[@id='results']/a")
    private WebElement searchResult;

    @FindBy(xpath = "//*[@id='results']/h5")
    private WebElement noCityFoundElement;


    // Constructor
    public HomePage(RemoteWebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, MAX_WAIT_TIME);
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
        this.ajax = new AjaxElementLocatorFactory(driver, MAX_WAIT_TIME);
        PageFactory.initElements(ajax, this);
    }


    // Actions
    public void navigateToHomePage() {
        if (!driver.getCurrentUrl().equals(HOMEPAGE_URL)) {
        this.driver.get(HOMEPAGE_URL);
        }
        // this.driver.get(HOMEPAGE_URL);

    }

    public void clickOnRegisterButton() {
        try {
            wait.until(ExpectedConditions.visibilityOf(registerButton)).click();
            wait.until(ExpectedConditions.urlContains("register"));

        } catch (Exception e) {
            UtilityMethods.logError("HomePage", "Error: Failed to click on register button",
                    "failed", e);
        }
    }


    public void clickOnLoginButton() {
        try {
            wait.until(ExpectedConditions.visibilityOf(loginHereButton)).click();
            wait.until(ExpectedConditions.urlContains("login"));

        } catch (Exception e) {
            UtilityMethods.logError("HomePage", "Error: Failed to click on login button", "failed",
                    e);
        }
    }

    public boolean isUserLoggedIn() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(logoutButton)).isDisplayed();

        } catch (Exception e) {
            UtilityMethods.logError("HomePage", "Error: while checking is user logged in", "failed",
                    e);
            return false;
        }
    }

    public void logoutUser() {
        try {
            wait.until(ExpectedConditions.visibilityOf(logoutButton)).click();
            wait.until(ExpectedConditions.visibilityOf(registerButton));

        } catch (Exception e) {
            UtilityMethods.logError("HomePage", "Error: while performing logout", "failed", e);
        }
    }


    public boolean isUserLoggedOut() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(registerButton)).isDisplayed();

        } catch (Exception e) {
            UtilityMethods.logError("HomePage", "Error: while checking is user logged out",
                    "failed", e);
            return false;
        }
    }


    public void searchCity(String cityName) {
        try {
            wait.until(ExpectedConditions.visibilityOf(searchBox));

            searchBox.clear();
            searchBox.sendKeys(cityName);

            actions.click(searchBox).sendKeys(" ").sendKeys(Keys.BACK_SPACE).perform();
            actions.click(searchBox).sendKeys(" ").perform();


            int retryCount = 0;
            try {
                while (retryCount < 3) {
                    try {
                        AjaxElementLocatorFactory ajax = new AjaxElementLocatorFactory(driver, 10);
                        PageFactory.initElements(ajax, this);

                        wait.until(ExpectedConditions.or(
                                ExpectedConditions.visibilityOf(noCityFoundElement),
                                ExpectedConditions.visibilityOf(searchResult)));


                        System.out.println("SearchCity element found...");
                        return;

                    } catch (StaleElementReferenceException | TimeoutException | NoSuchElementException ex) {
                        retryCount++;
                        System.out.println("Retrying due to stale element in searchCity... attempt "
                                + retryCount);
                        if (retryCount > 3) {
                            throw new StaleElementReferenceException(
                                    "Could not resolve stale element 'searchCity' after "
                                            + retryCount + " attempts");
                        }
                    } 
                }

            } catch (Exception e) {
                UtilityMethods.logError("HomePage", "Error: while searching for a city", "failed",
                        e);
            }

        } catch (Exception e) {
            UtilityMethods.logError("HomePage", "Error: while searching for a city", "failed", e);

        }
    }



    public void selectCity(String cityName) {
        int retryCount = 0;

        try {
            while (retryCount < 3) {
                try {
                    status = wait.until(driver -> {
                        AjaxElementLocatorFactory ajax = new AjaxElementLocatorFactory(driver, 10);
                        PageFactory.initElements(ajax, this); // refresh stale elements

                        return searchResult.getText().trim().equalsIgnoreCase(cityName);
                    });


                    if (status) {
                        System.out.println("selectCity element found... ");
                        searchResult.click();
                    } else {
                        UtilityMethods.logStatus("HomePage", "Error: Failed to select city",
                                "failed");
                    }

                    wait.until(ExpectedConditions.urlContains(cityName.toLowerCase()));
                    return; // success, exit method

                } catch (StaleElementReferenceException e) {
                    retryCount++;
                    System.out.println(
                            "Retrying due to stale element in selectCity... attempt " + retryCount);
                    if (retryCount > 3) {
                        throw new StaleElementReferenceException(
                                "Could not resolve stale element 'searchResult' after " + retryCount
                                        + " attempts");
                    }
                } catch (Exception e) {
                    UtilityMethods.logError("HomePage",
                            "Error: while selecting city from search result", "failed", e);
                    return;
                }
            }
        } catch (Exception e) {
            UtilityMethods.logError("HomePage",
                    "Error: Could not resolve stale element 'searchResult' even after " + retryCount
                            + " retries",
                    "failed", e);
        }
    }



    public boolean isNoCityFoundTextDisplayed() {
        int retryCount = 0;

        try {
            while (retryCount < 3) {

                try {
                    status = wait.until(driver -> {
                        AjaxElementLocatorFactory ajax = new AjaxElementLocatorFactory(driver, 10);
                        PageFactory.initElements(ajax, this);

                        return noCityFoundElement.isDisplayed();
                    });
                    if (status) {
                        System.out.println("'noCityFoundElement' found...");
                    }
                    return status;

                } catch (StaleElementReferenceException e) { // to catch stale element for retry
                    retryCount++;
                    System.out.println(
                            "Retrying due to stale element for 'noCityFoundElement'... attempt "
                                    + retryCount);
                    if (retryCount > 3) {
                        throw new StaleElementReferenceException(
                                "Could not resolve StaleElement for 'noCityFoundElement' element after retrying '"
                                        + retryCount + "' times");
                    }

                } catch (Exception e) {
                    UtilityMethods.logError("HomePage",
                            "Error: while validating 'No City found' message", "failed", e);
                    return false;
                }
            }

        } catch (Exception e) {
            UtilityMethods.logError("HomePage",
                    "Error: Could not resolve StaleElement for 'noCityFoundElement' element even after retrying '"
                            + retryCount + "' times'",
                    "failed", e);
            return false;
        }
        return false;
    }



    // checks if the auto complete result contains the given city
    public boolean assertAutoCompleteText(String city) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(searchResult)).getText().toLowerCase()
                    .contains(city.toLowerCase());

        } catch (Exception e) {
            UtilityMethods.logError("HomePage",
                    "Error: while checking if city is displayed on Auto complete", "failed", e);
            return false;
        }
    }



}



/*
 * public void selectCity(String cityName) { int retryCount = 0;
 * 
 * try { while (retryCount < 3) { try { status = wait.until(driver -> { AjaxElementLocatorFactory
 * ajax = new AjaxElementLocatorFactory(driver, 10); PageFactory.initElements(ajax, this); //
 * refresh stale elements
 * 
 * return searchResult.getText().trim().equalsIgnoreCase(cityName); });
 * 
 * // Log and act on match System.out.println("City name from webElement: " +
 * searchResult.getText().trim()); System.out.println("Actual city name passed: " + cityName);
 * System.out.println("Match status: " + status);
 * 
 * if (status) { searchResult.click(); } else { UtilityMethods.logStatus("HomePage",
 * "Error: Failed to select a city", "failed"); }
 * 
 * wait.until(ExpectedConditions.urlContains(cityName.toLowerCase())); return; // success, exit
 * method
 * 
 * } catch (StaleElementReferenceException e) { retryCount++;
 * System.out.println("Retrying due to stale element in selectCity... attempt " + retryCount); if
 * (retryCount > 3) { throw new StaleElementReferenceException(
 * "Could not resolve stale element 'searchResult' after " + retryCount + " attempts"); } } catch
 * (Exception e) { UtilityMethods.logError("HomePage",
 * "Error: while selecting city from search result", "failed", e); return; } } } catch (Exception e)
 * { UtilityMethods.logError("HomePage",
 * "Error: Could not resolve stale element 'searchResult' even after " + retryCount + " retries",
 * "failed", e); } }
 * 
 */
