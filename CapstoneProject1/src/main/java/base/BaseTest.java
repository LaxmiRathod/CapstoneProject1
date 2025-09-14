package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.time.Duration;

public class BaseTest {
    protected static WebDriver driver;

    // Launch browser
    public void setup() {
        String driverPath = "C:\\Users\\Rajap\\eclipse\\chromedriver-win64\\chromedriver.exe";

        // Use local ChromeDriver if available
        if (new File(driverPath).exists()) {
            System.setProperty("webdriver.chrome.driver", driverPath);
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*"); // optional for CORS issues

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    // Close browser
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
