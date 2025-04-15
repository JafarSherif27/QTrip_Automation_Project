package qtriptest.pages;


import qtriptest.utils.UtilityMethods;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
// import java.util.Map;
// import java.util.concurrent.TimeoutException;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class HistoryPage {
    RemoteWebDriver driver;
    WebDriverWait wait;
    private boolean status;
    private static String lastGeneratedTransactionID;
    Actions actions;
    JavascriptExecutor js;
    private static final int MAX_WAIT_TIME = 10;


    // Locators
    @FindBy(xpath = "//li//a[contains(@href, 'reservation')]")
    private WebElement reservationPageElement;

    // Parent element for reservation details table
    @FindBy(id = "reservation-table-parent")
    private WebElement reservationTableParentElement;

    @FindBy(name = "person")
    private WebElement personFieldElement;

    @FindBy(id = "no-reservation-banner")
    private WebElement noReservationBannerElement;

    // Used to wait for table to load
    @FindBy(className = "reservation-visit-button")
    private WebElement visitAdventureElement;

    // @FindBy(xpath = "//a[contains(text(), 'Reservations')]")
    // private WebElement reservationLinkElement;


    // Constructor
    public HistoryPage(RemoteWebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, MAX_WAIT_TIME);
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
        AjaxElementLocatorFactory ajax = new AjaxElementLocatorFactory(driver, MAX_WAIT_TIME);
        PageFactory.initElements(ajax, this);
    }


    // Actions - meathods
    public void navigateToHistoryPage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(reservationPageElement)).click();

        } catch (Exception e) {
            UtilityMethods.logError("HistoryPage",
                    "Error: While trying to click on Reservation button", "failed", e);
        }
    }


    public String getLastGeneratedTransactionId() {
        return lastGeneratedTransactionID;
    }


    // Updated CancelReservation method with retry for stale element and optional alert handling
    public void CancelReservation(String transactionId) {
        By cancelButton = By.xpath("//button[@id='" + transactionId + "']");
        int attempts = 0;

        while (attempts < 2) {
            try {
                WebElement cancelBtn =
                        wait.until(ExpectedConditions.elementToBeClickable(cancelButton));
                cancelBtn.click();

                if (isAlertPresent()) {
                    Alert alert = driver.switchTo().alert();
                    System.out.println("Alert message (Cancellation): " + alert.getText());
                    alert.accept();
                }

                break; // if successful, exit the loop

            } catch (StaleElementReferenceException staleEx) {
                System.out.println("StaleElementReferenceException caught. Retrying...");
                attempts++;
            } catch (Exception e) {
                UtilityMethods.logError("HistoryPage",
                        "Error: While trying to cancel the reservation for transactionID '"
                                + transactionId + "'",
                        "failed", e);
                break;
            }
        }
    }



    public boolean isCancellationSuccess(String transactionId) {

        try {
            By cancelButton = By.xpath("//button[@id='" + transactionId + "']");

            AjaxElementLocatorFactory ajax = new AjaxElementLocatorFactory(driver, 10);
            PageFactory.initElements(ajax, this);
            status = wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOf(noReservationBannerElement),
                ExpectedConditions.invisibilityOfElementLocated(cancelButton)));

            // status = wait.until(ExpectedConditions.invisibilityOfElementLocated(cancelButton));

            if (isAlertPresent()) {
                Alert alert = driver.switchTo().alert();
                System.out.println("Alert message (Cancellation): " + alert.getText());
                alert.accept();
            }

            return status;

        } catch (Exception e) {
            UtilityMethods.logError("HistoryPage",
                    "Error: While trying to cancel the reservation for transactionID '"
                            + transactionId + "'",
                    "failed", e);
        }
        return true;
    }


    public boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    public List<HashMap<String, String>> GetReservations() {
        List<HashMap<String, String>> reservationDetails = new ArrayList<>();

        try {

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tbody//th")));

            String[] headerTitles =
                    {"Transaction ID", "Booking Name", "Adventure", "Person(s)", "Date", "Price"};

            List<WebElement> rows = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//tbody//tr")));

            for (WebElement row : rows) {

                HashMap<String, String> map = new HashMap<>();

                for (int i = 0; i < headerTitles.length; i++) {
                    WebElement transactionIdElement = row.findElement(By.xpath(".//th"));
                    if (i == 0) {
                        map.put(headerTitles[i], transactionIdElement.getText());
                        lastGeneratedTransactionID = transactionIdElement.getText();

                    } else {
                        WebElement cellValue = transactionIdElement.findElement(
                                By.xpath(".//following-sibling::td[position()=" + i + "]"));
                        map.put(headerTitles[i], cellValue.getText());
                    }
                }
                reservationDetails.add(map);
            }

            if (reservationDetails.size() == 0) {
                System.out.println("reservationDetails.size(): " + reservationDetails.size());
                System.out.println("Reservation values are not fetched properly");
                lastGeneratedTransactionID = null;
            }
            
            return reservationDetails;


        } catch (Exception e) {
            UtilityMethods.logError("HistoryPage",
                    "Error: While trying to get the reservation details", "failed", e);
            return null;
        }
    }

}



// public List<HashMap<String, String>> GetReservations() {
// List<HashMap<String, String>> reservationDetails = new ArrayList<>();

// try {

// wait.until(ExpectedConditions.elementToBeClickable(visitAdventureElement));

// // This includes Header row as well
// List<WebElement> noOfRowsInTable =
// wait.until(ExpectedConditions.visibilityOfAllElements(
// reservationTableParentElement.findElements(By.xpath(".//tr"))));

// int noOfReservation = noOfRowsInTable.size() - 1;

// if (noOfReservation == 0) {
// UtilityMethods.logStatus("HistoryPage", "There is no reservation made",
// "No details found");
// // return null;
// }

// List<WebElement> tableHeaders =
// reservationTableParentElement.findElements(By.xpath(".//thead//th"));
// List<WebElement> rowsElement =
// reservationTableParentElement.findElements(By.xpath(".//tbody//tr"));

// for (WebElement row : rowsElement) {

// HashMap<String, String> reservation = new HashMap<>();

// // wait.until(ExpectedConditions.visibilityOfAllElements(row.findElements(By.xpath("./th"))));
// // wait.until(ExpectedConditions.visibilityOfAllElements(row.findElements(By.xpath("./td"))));

// List<WebElement> reservationIdList = row.findElements(By.xpath("./th"));
// List<WebElement> cellValue = row.findElements(By.xpath(".//td"));

// // only getting first 6 cell values from reservation table
// for (int i = 0; i < 6; i++) {
// // Get headers in each cell and map it to values of each row
// String headerTitle = tableHeaders.get(i).getText().trim();
// String value = "";

// if (i == 0) {
// value = reservationIdList.get(i).getText().trim();
// lastGeneratedTransactionID = value;
// } else {
// value = cellValue.get(i).getText().trim();
// }
// reservation.put(headerTitle, value);
// }

// // add details of reservation to list
// reservationDetails.add(reservation);
// }

// if (reservationDetails.size() == 0) {
// lastGeneratedTransactionID = null;
// }

// System.out.println(reservationDetails);
// return reservationDetails;


// } catch (Exception e) {
// UtilityMethods.logError("HistoryPage",
// "Error: While trying to get the reservation details", "failed", e);
// return null;
// }
// }



// // transactionId - is the transaction ID of the reservation that you want to cancel
// public void CancelReservation(String transactionId) {
// try {
// By cancelButton = By.xpath("//button[@id='" + transactionId + "']");
// wait.until(ExpectedConditions.visibilityOfElementLocated(cancelButton)).click();


// } catch (NoAlertPresentException ne) {
// //do nothing
// System.out.println("Alert not present");

// }catch (UnhandledAlertException ae) {
// Alert alert = driver.switchTo().alert();
// System.out.println("Alert message: " + alert.getText());
// alert.accept();

// } catch (Exception e) {
// UtilityMethods.logError("HistoryPage",
// "Error: While trying to cancel the reservation for transactionID '"
// + transactionId + "'",
// "failed", e);
// }
// }


