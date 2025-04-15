package qtriptest.pages;

import java.util.UUID;
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

@SuppressWarnings("unused")
public class RegisterPage {
    RemoteWebDriver driver;
    WebDriverWait wait;
    Actions actions;
    JavascriptExecutor js;
    private boolean status;
    public String lastGeneratedEmail;
    private static final int MAX_WAIT_TIME = 10;
    private static final String REGISTER_PAGE_URL =
            "https://qtripdynamic-qa-frontend.vercel.app/pages/register/";
    private static final String LOGIN_PAGE_URL =
            "https://qtripdynamic-qa-frontend.vercel.app/pages/login/";

    // Locators
    @FindBy(name = "email")
    private WebElement emailElement;

    @FindBy(name = "password")
    private WebElement passwordElement;

    @FindBy(name = "confirmpassword")
    private WebElement confirmPasswordElement;

    @FindBy(xpath = "//button[normalize-space(text())='Register Now']")
    private WebElement registerNowButton;

    // Locator for "Register" text in the Registration page
    @FindBy(xpath = "//h2[text()='Register']")
    private WebElement registerTextElement;



    // Constructor
    public RegisterPage(RemoteWebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, MAX_WAIT_TIME);
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
        AjaxElementLocatorFactory ajax = new AjaxElementLocatorFactory(driver, MAX_WAIT_TIME);
        PageFactory.initElements(ajax, this);
    }

    // Actions
    public void navigateToRegisterPage() {
        if (!driver.getCurrentUrl().contains("register")) {
            this.driver.get(REGISTER_PAGE_URL);
        }
    }


    public boolean registerNewUser(String email, String password, String confirmPassword,
            boolean isEmailDynamic) {

        String userEmailInput = email;

        try {
            if (isEmailDynamic) {
                userEmailInput =
                        String.format("testuser%s@gmail.com", UUID.randomUUID().toString());                        
                System.out.println("USER DYNAMIC EMAIL IS:: "+userEmailInput);
            }else{
                // System.out.println("USER EMAIL IS:: "+userEmailInput);
            }
            lastGeneratedEmail = userEmailInput;

            
            // System.out.println("USER PASSWORD IS:: "+password);
            // System.out.println("USER CONFIRM PASSWORD IS:: "+confirmPassword);

            wait.until(ExpectedConditions.visibilityOf(emailElement)).sendKeys(userEmailInput);
            wait.until(ExpectedConditions.visibilityOf(passwordElement)).sendKeys(password);
            wait.until(ExpectedConditions.visibilityOf(confirmPasswordElement))
                    .sendKeys(confirmPassword);
            wait.until(ExpectedConditions.visibilityOf(registerNowButton)).click();

            return wait.until(ExpectedConditions.urlContains("login"));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUserNavigatedToRegisterPage() {
        try {
            status = driver.getCurrentUrl().contains("register")
                    && registerTextElement.isDisplayed();
            return status;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
