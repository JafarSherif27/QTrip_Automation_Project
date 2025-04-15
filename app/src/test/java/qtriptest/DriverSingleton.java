package qtriptest;

import net.bytebuddy.agent.builder.AgentBuilder.Default;
import java.net.URL;
import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

@SuppressWarnings("unused")
public class DriverSingleton {
    private static DriverSingleton singleton;
    private static RemoteWebDriver driver;
    private static WebDriverWait wait;
    private static Actions actions;
    private static JavascriptExecutor js;
    private static final int MAX_WAIT_TIME = 15;

    private DriverSingleton() {}


    public static DriverSingleton getSingletonInstance() {
        try {
            if (singleton == null) {
                singleton = new DriverSingleton();
            }
            return singleton;

        } catch (Exception e) {
            System.out.println("Error: While creating object of DriverSingleton class");
            e.printStackTrace();
            return singleton;
        }

    }


    public static RemoteWebDriver getDriverInstance(String browserName) {
        try {
            if (driver == null) {

                switch (browserName.toUpperCase()) {

                    case "CHROME":
                        DesiredCapabilities chromeCapabilities = new DesiredCapabilities();
                        chromeCapabilities.setBrowserName(BrowserType.CHROME);
                        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), chromeCapabilities);
                        break;
                
                    case "FIREFOX":
                        DesiredCapabilities firefoxCapabilities = new DesiredCapabilities();
                        firefoxCapabilities.setBrowserName(BrowserType.FIREFOX);
                        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), firefoxCapabilities);
                        break;
                
                    case "EDGE":
                        DesiredCapabilities edgeCapabilities = new DesiredCapabilities();
                        edgeCapabilities.setBrowserName(BrowserType.EDGE);
                        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), edgeCapabilities);
                        break;
                
                    default:
                        System.out.println("Error: There is no driver implementation given for the given browser '"+browserName+"'");
                        break;
                }
            }
            return driver;
            
        } catch (Exception e) {
            System.out.println("Error: While creating the driver object");
            e.printStackTrace();
            return driver;
        }
  
    }


    public static WebDriverWait getWebDriverWaitInstance() {
        try {
            if (wait == null && driver != null) {
                wait = new WebDriverWait(driver, MAX_WAIT_TIME);
            }
            if (driver == null) {
                System.out.println(
                        "Error: Driver instance is not created - Initialize driver instance first");
            }
            return wait;

        } catch (Exception e) {
            System.out.println("Error: While creating object of WebDriverWait");
            e.printStackTrace();
            return wait;
        }
        
    }


    public static Actions getActionsClassInstance() {
        try {
            if (actions == null && driver != null) {
                actions = new Actions(driver);
            }
            if (driver == null) {
                System.out.println(
                        "Error: Driver instance is not created - Initialize driver instance first");
            }
            return actions;    

        } catch (Exception e) {
            System.out.println("Error: While creating object of Actions class");
            e.printStackTrace();
            return actions;
        }

    }


    public static JavascriptExecutor getJsExecutorInstance(){
        try {
            if(js == null && driver != null){
                js = (JavascriptExecutor) driver;
            }
            if (driver == null) {
                System.out.println(
                        "Error: Driver instance is not created - Initialize driver instance first");
            }
            return js;
            
        } catch (Exception e) {
            System.out.println("Error: While creating object of JavascriptExecutor");
            e.printStackTrace();
            return js;
        }

    }



}
