package qtriptest.pages;

import qtriptest.utils.UtilityMethods;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class AdventureDetailsPage {
    RemoteWebDriver driver;
    WebDriverWait wait;
    private boolean status;
    Actions actions;
    JavascriptExecutor js;
    private static final int MAX_WAIT_TIME = 10;


    // Locators
    @FindBy(name = "name")
    private WebElement nameFieldElement;

    @FindBy(name = "date")
    private WebElement dateFieldElement;

    // Number of people
    @FindBy(name = "person")
    private WebElement personFieldElement;

    @FindBy(className = "reserve-button")
    private WebElement reserveButtonElement;

    // Reservation success message
    @FindBy(id = "reserved-banner")
    private WebElement reservedSuccessElement;

    @FindBy(xpath = "//a[contains(text(), 'Reservations')]")
    private WebElement reservationLinkElement;

    @FindBy(id = "reservation-person-cost")
    private WebElement costPerPersonElement;

    @FindBy(id = "reservation-cost")
    private WebElement totalCostElement;


    // Constructor
    public AdventureDetailsPage(RemoteWebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, MAX_WAIT_TIME);
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
        AjaxElementLocatorFactory ajax = new AjaxElementLocatorFactory(driver, MAX_WAIT_TIME);
        PageFactory.initElements(ajax, this);
    }


    // Actions - meathods
    public void BookAdventure(String guestName, String date, String noOfPeople) {

        try {

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            // Parse the string into a LocalDate object
            LocalDate dateObj = LocalDate.parse(date, inputFormatter);
            // Define the target format
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            // Format the date into the new format
            String formattedDateStr = dateObj.format(outputFormatter);


            wait.until(ExpectedConditions.visibilityOf(nameFieldElement)).clear();
            nameFieldElement.sendKeys(guestName);
            wait.until(ExpectedConditions.visibilityOf(dateFieldElement));

            System.out.println("formattedDateStr: " + formattedDateStr);
            // js.executeScript("arguments[0].value= arguments[1];",
            // dateFieldElement,formattedDateStr);
            // actions.click(dateFieldElement).sendKeys(formattedDateStr).perform();

            dateFieldElement.click();
            dateFieldElement.sendKeys(formattedDateStr);

            wait.until(ExpectedConditions.visibilityOf(personFieldElement)).clear();
            personFieldElement.sendKeys(noOfPeople);

            int costPerPerson = Integer.parseInt(wait
                    .until(ExpectedConditions.visibilityOf(costPerPersonElement)).getText().trim());
            int expectedTotalCost = Integer.parseInt(noOfPeople.trim()) * costPerPerson;

            // Total cost displayed on webpage should need to be equal to the expectedTotalCost
            status = wait.until(ExpectedConditions.textToBePresentInElement(totalCostElement,
                    String.valueOf(expectedTotalCost)));

            if (status) {
                wait.until(ExpectedConditions.elementToBeClickable(reserveButtonElement)).click();
            }


        } catch (Exception e) {
            UtilityMethods.logError("AdventureDetailsPage", "Error: Failed to Book adventure",
                    "failed", e);
        }

    }


    public boolean isBookingSuccessful() {
        try {
            int retryCount = 0;
            while (retryCount < 3) {   
                try {
                    AjaxElementLocatorFactory ajax = new AjaxElementLocatorFactory(driver, 10);
                    PageFactory.initElements(ajax, this);

                    status = wait.until(ExpectedConditions.visibilityOf(reservedSuccessElement))
                            .getText().contains("Reservation for this adventure is successful");
                    return status;

                } catch (StaleElementReferenceException e) {
                    retryCount++;
                        System.out.println("Retrying due to stale element in isBookingSuccessful... attempt "
                                + retryCount);
                        if (retryCount > 3) {
                            throw new StaleElementReferenceException(
                                    "Could not resolve stale element 'isBookingSuccessful' after "
                                            + retryCount + " attempts");
                        }
                }
            } 

        }catch (Exception e) {
            UtilityMethods.logError("AdventureDetailsPage",
                    "Error: Failed to check is booking successful", "failed", e);
            return false;
        }
        return status;

    }


}