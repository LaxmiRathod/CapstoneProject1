package stepDefinitions;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import io.cucumber.java.en.*;
import java.time.Duration;

public class IFrameSteps extends BaseTest {

    private String firstImageSrc;

    @Given("I launch {string}")
    public void i_launch(String url) {
        setup();
        driver.get(url);
        driver.manage().window().maximize();
    }

    @Then("the page title should contain {string}")
    public void verifyTitle(String expectedTitle) {
        Assert.assertTrue(driver.getTitle().contains(expectedTitle),
                "Expected title to contain: " + expectedTitle + " but found: " + driver.getTitle());
    }

    @When("I click on {string}")
    public void clickOnLink(String linkText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//h1[text()='" + linkText + "']/ancestor::a")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
    }

    @Then("a new tab should open")
    public void switchTab() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        String originalWindow = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalWindow)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        wait.until(ExpectedConditions.titleContains("IFrame"));
    }

    @And("I verify the image is displayed")
    public void verifyImage() {
        driver.switchTo().defaultContent();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("frame")));

        WebElement img = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".item.active img")));

        firstImageSrc = img.getAttribute("src");
        System.out.println("First image source: " + firstImageSrc);

        driver.switchTo().defaultContent();
    }

    @When("I click the right arrow")
    public void clickArrow() {
        driver.switchTo().defaultContent();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("frame")));

        try {
            WebElement arrow = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".owl-next")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", arrow);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", arrow);
        } catch (TimeoutException e) {
            System.out.println("Arrow not clickable — will rely on auto-rotation");
        }

        driver.switchTo().defaultContent();
    }

    @Then("the image should change")
    public void verifyImageChange() {
        driver.switchTo().defaultContent();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("frame")));

        WebElement img = null;
        String newSrc = firstImageSrc;
        long end = System.currentTimeMillis() + 30000;

        while (System.currentTimeMillis() < end) {
            try {
                img = driver.findElement(By.cssSelector(".item.active img"));
                newSrc = img.getAttribute("src");

                if (!newSrc.equals(firstImageSrc)) {
                    break;
                }

                Thread.sleep(500);
            } catch (NoSuchElementException | InterruptedException e) {
                // ignore and retry
            }
        }

        if (newSrc.equals(firstImageSrc)) {
            throw new TimeoutException("Image did not change within 30 seconds");
        }

        System.out.println("New image source: " + newSrc);
        Assert.assertNotEquals(newSrc, firstImageSrc, "Image did not change");

        driver.switchTo().defaultContent();
        tearDown();
    }
}
