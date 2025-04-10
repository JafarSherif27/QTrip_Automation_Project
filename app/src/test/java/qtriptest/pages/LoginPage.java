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

public class LoginPage {
    RemoteWebDriver driver;
    WebDriverWait wait;
    Actions actions;
    JavascriptExecutor js;
    private static final int MAX_WAIT_TIME = 10;
    private static final String LOGIN_PAGE_URL =
            "https://qtripdynamic-qa-frontend.vercel.app/pages/login/";
    private static final String HOMEPAGE_URL = "https://qtripdynamic-qa-frontend.vercel.app";

    // Locators
    @FindBy(name = "email")
    private WebElement emailElement;

    @FindBy(name = "password")
    private WebElement passwordElement;

    @FindBy(xpath = "//button[normalize-space(text())='Login to QTrip']")
    private WebElement loginToQtripButton;

    //Locator for "Login" text in the Login page 
    @FindBy(xpath ="//h2[text()='Login']")
    private WebElement loginTextElement;


    // Constructor
    public LoginPage(RemoteWebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, MAX_WAIT_TIME);
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
        AjaxElementLocatorFactory ajax = new AjaxElementLocatorFactory(driver, MAX_WAIT_TIME);
        PageFactory.initElements(ajax, this);
    }


    // Actions
    public void navigateToLoginPage() {
        if (!driver.getCurrentUrl().contains("login")) {
            this.driver.get(LOGIN_PAGE_URL);
        }
    }

    public void performLogin(String email, String password) {

        try {
            // System.out.println("USER EMAIL IS:: "+email);
            // System.out.println("USER PASSWORD IS:: "+password);
            wait.until(ExpectedConditions.visibilityOf(emailElement)).sendKeys(email);
            wait.until(ExpectedConditions.visibilityOf(passwordElement)).sendKeys(password);
            wait.until(ExpectedConditions.elementToBeClickable(loginToQtripButton)).click();

            wait.until(ExpectedConditions.urlContains(HOMEPAGE_URL));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean isUserNavigatedToLoginPage() {
        try {
            return driver.getCurrentUrl().contains("login") && loginTextElement.isDisplayed();
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
       
    }



}
