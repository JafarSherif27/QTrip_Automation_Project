package qtriptest.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage {
    RemoteWebDriver driver;
    WebDriverWait wait;
    Actions actions;
    JavascriptExecutor js;
    private static final int MAX_WAIT_TIME = 10;
    private static final String HOMEPAGE_URL = "https://qtripdynamic-qa-frontend.vercel.app";



    // Locators
    @FindBy(xpath = "//a[normalize-space(text())='Register']")
    private WebElement registerButton;

    @FindBy(xpath = "//div[normalize-space(text())='Logout']")
    private WebElement logoutButton;

    @FindBy(xpath="//a[normalize-space(text())='Login Here']")
    private WebElement loginHereButton;


    // Constructor
    public HomePage(RemoteWebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, MAX_WAIT_TIME);
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
        AjaxElementLocatorFactory ajax = new AjaxElementLocatorFactory(driver, MAX_WAIT_TIME);
        PageFactory.initElements(ajax, this);
    }


    // Actions
    public void navigateToHomePage() {
        if (!driver.getCurrentUrl().contains(HOMEPAGE_URL)) {
            this.driver.get(HOMEPAGE_URL);
        }
    }

    public void clickOnRegisterButton() {
        try {
            wait.until(ExpectedConditions.visibilityOf(registerButton)).click();
            wait.until(ExpectedConditions.urlContains("register"));
    
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clickOnLoginButton() {
        try {
            wait.until(ExpectedConditions.visibilityOf(loginHereButton)).click();
            wait.until(ExpectedConditions.urlContains("login"));
    
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isUserLoggedIn() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(logoutButton)).isDisplayed();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void logoutUser() {
        try {
            wait.until(ExpectedConditions.visibilityOf(logoutButton)).click();

            wait.until(ExpectedConditions.visibilityOf(registerButton));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isUserLoggedOut() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(registerButton)).isDisplayed();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        

    }



    /*
     * isUserLoggedIn boolean 
     * logOutUser 
     * searchCity(string) 
     * assertAutoCompleteText(string) boolean
     * selectCity(string)
     */



}
