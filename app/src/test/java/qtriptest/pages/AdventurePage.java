package qtriptest.pages;

import qtriptest.utils.UtilityMethods;
import java.io.ObjectInputFilter.Status;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

@SuppressWarnings("unused")
public class AdventurePage {
    RemoteWebDriver driver;
    WebDriverWait wait;
    private boolean status;
    Actions actions;
    JavascriptExecutor js;
    private static final int MAX_WAIT_TIME = 15;
    // private static final String HOMEPAGE_URL = "https://qtripdynamic-qa-frontend.vercel.app";



    // Locators
    @FindBy(id = "duration-select")
    private WebElement filterByDurationElement;

    @FindBy(xpath = "//select[@id='duration-select']/following-sibling::div[contains(normalize-space(text()), 'Clear')]")
    private WebElement clearFilterButton;

    @FindBy(id = "category-select")
    private WebElement categoryElement;

    @FindBy(xpath = "//select[@id='category-select']/following-sibling::div[contains(normalize-space(text()), 'Clear')]")
    private WebElement clearCategoryButton;

    // gets all the parent element of search results
    @FindBy(xpath = "//div[@id='data']//a")
    private List<WebElement> parentElementOfResults;

    // gets all the category banner elements of the search results
    @FindBy(className = "category-banner")
    private List<WebElement> categoryBanner;

    // get the selected category tags that appears on web after selecting a category
    @FindBy(className = "category-filter")
    private List<WebElement> selectedCategoryTag;

    // Adventure title element after click on an adventure - for wait
    @FindBy(id = "adventure-name")
    private WebElement adventureTitleElement;


    // Constructor
    public AdventurePage(RemoteWebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, MAX_WAIT_TIME);
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
        AjaxElementLocatorFactory ajax = new AjaxElementLocatorFactory(driver, MAX_WAIT_TIME);
        PageFactory.initElements(ajax, this);
    }



    public void selectFilterByDuration(String duration) {
        try {
            wait.until(ExpectedConditions.visibilityOf(clearFilterButton)).click();

            wait.until(ExpectedConditions.visibilityOf(filterByDurationElement));
            Select select = new Select(filterByDurationElement);
            select.selectByVisibleText(duration);

            wait.until(driver -> {
                return select.getFirstSelectedOption().getText().equals(duration);
            });

        } catch (Exception e) {
            UtilityMethods.logError("AdventurePage",
                    "Error: while selecting given filter by duration '" + duration + "'", "failed",
                    e);
        }

    }


    public void selectCategory(String categoryToSelect) {
        try {
            wait.until(ExpectedConditions.visibilityOf(clearCategoryButton)).click();

            wait.until(ExpectedConditions.visibilityOf(categoryElement));
            Select select = new Select(categoryElement);
            select.selectByVisibleText(categoryToSelect.trim());

            wait.until(driver -> {
                for (WebElement ele : selectedCategoryTag) {
                    status = categoryToSelect.trim().contains(ele.getText().trim());

                    if (status) {
                        return status;
                    }
                }
                return false;
            });



        } catch (Exception e) {
            UtilityMethods.logError("AdventurePage",
                    "Error: while selecting given category '" + categoryToSelect + "'", "failed",
                    e);
        }
    }


    public void clearAllFilterAndCategory() {
        try {
            wait.until(ExpectedConditions.visibilityOf(clearFilterButton)).click();
            wait.until(ExpectedConditions.visibilityOf(clearCategoryButton)).click();

        } catch (Exception e) {
            UtilityMethods.logError("AdventurePage",
                    "Error: while clearing duration filter and category applied", "failed", e);
        }
    }



    public boolean verifySelectedFilter(String durationFilter) {
        try {
            int lowerDuration = Integer.parseInt(durationFilter.trim().split("-")[0]);
            int upperDuration = Integer.parseInt(durationFilter.trim().split("-")[1].split(" ")[0]);


            List<WebElement> elements =
                    wait.until(ExpectedConditions.visibilityOfAllElements(parentElementOfResults));

            for (WebElement ele : elements) {
                List<WebElement> childElements = ele.findElements(By.xpath(".//p"));
                int actualDuration =
                        Integer.parseInt(childElements.get(1).getText().trim().split(" ")[0]);

                if (actualDuration >= lowerDuration && actualDuration <= upperDuration) {
                    status = true;
                }
                if (!status) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            UtilityMethods.logError("AdventurePage",
                    "Error: while verifying applied duration filter", "failed", e);
            return false;
        }
    }


    public boolean verifySelectedCategory(String category) {
        try {
            for (WebElement CategoryEle : categoryBanner) {
                String categoryBanner = CategoryEle.getText().trim();
                status = category.contains(categoryBanner);

                if (!status) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            UtilityMethods.logError("AdventurePage", "Error: while verifying applied category",
                    "failed", e);
            return false;
        }
    }


    // return total number of results currently displaying on the page
    public int getTotalNoOfResults() {
        int totalSearchResults = 0;
        try {
            return wait.until(ExpectedConditions.visibilityOfAllElements(parentElementOfResults))
                    .size();

        } catch (Exception e) {
            UtilityMethods.logError("AdventurePage",
                    "Error: while fetching toatal number of search results", "failed", e);

            return totalSearchResults;
        }
    }


    public void selectAdventure(String adventureToSelect) {
        try {
            List<WebElement> elements =
                    wait.until(ExpectedConditions.visibilityOfAllElements(parentElementOfResults));

            for (int i = 0; i < elements.size(); i++) {
                int retryCount = 0;
                while (retryCount < 3) {
                    try {
                        WebElement ele = wait.until(
                                ExpectedConditions.visibilityOfAllElements(parentElementOfResults))
                                .get(i);
                        WebElement adventureElement = ele.findElement(By.xpath(".//h5"));

                        if (adventureElement.getText().equalsIgnoreCase(adventureToSelect)) {
                            adventureElement.click();
                            wait.until(ExpectedConditions.visibilityOf(adventureTitleElement));
                            return;
                        }

                        break; // Break the retry loop if no exception and not matching - to move to next element 
                    } catch (StaleElementReferenceException e) {
                        retryCount++;
                        System.out.println(
                                "Retrying due to stale element in selectAdventure... attempt "
                                        + retryCount);
                    }
                }
            }

        } catch (Exception e) {
            UtilityMethods.logError("AdventurePage",
                    "Error: while selecting given adventure '" + adventureToSelect + "'", "failed",
                    e);
        }
    }


    // public void selectAdventure(String adventureToSelect) {
    // try {

    // List<WebElement> elements =
    // wait.until(ExpectedConditions.visibilityOfAllElements(parentElementOfResults));
    // for (WebElement ele : elements) {
    // int retryCount = 0;
    // while (retryCount < 3) {
    // try {
    // WebElement adventureElement = ele.findElement(By.xpath(".//h5"));
    // if (adventureElement.getText().equalsIgnoreCase(adventureToSelect)) {
    // adventureElement.click();
    // return;
    // }

    // } catch (StaleElementReferenceException e) {
    // retryCount++;
    // System.out.println(
    // "Retrying due to stale element in selectAdventure... attempt "
    // + retryCount);
    // // Re-fetch the list and the element to recover from stale state
    // elements = wait.until(
    // ExpectedConditions.visibilityOfAllElements(parentElementOfResults));
    // ele = elements.get(elements.indexOf(ele)); // Re-point to the same index

    // if (retryCount > 3) {
    // throw new StaleElementReferenceException(
    // "Could not resolve stale element 'selectAdventure' after "
    // + retryCount + " attempts");
    // }
    // }
    // }

    // }

    // wait.until(ExpectedConditions.visibilityOf(adventureTitleElement));

    // } catch (Exception e) {
    // UtilityMethods.logError("AdventurePage",
    // "Error: while selecting given adventure '" + adventureToSelect + "'", "failed",
    // e);
    // }
    // }



}
