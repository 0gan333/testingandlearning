package MavenProject.testingandlearning;

import java.io.File;
import java.time.Duration;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class estingTest {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        driver.get("https://with-bugs.practicesoftwaretesting.com/#/");
        System.out.println("Page opened successfully.");
        
    }

    @AfterMethod
    public void afterMethod() {
        try {
            Thread.sleep(5000); // 5-second break after each test
            System.out.println("Test completed, waiting for 5 seconds before next test...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
  

    @Test(priority = 1)
    public void checkTitle() throws InterruptedException {
        String expectedTitle = "Practice Software Testing - Toolshop - v5.0 with bugs";
        String actualTitle = driver.getTitle();
        Assert.assertEquals(actualTitle, expectedTitle, "The title of the page is not as expected.");

        WebElement slider = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span[aria-label='ngx-slider']")));
        WebElement slider2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@aria-label='ngx-slider-max']")));

        Actions action = new Actions(driver);
        action.dragAndDropBy(slider, 50, 0).perform(); // Move slider by 50 pixels
        Thread.sleep(2000);  // Wait for effect

        // Move the slider to its maximum value
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.left = '100%'", slider2); 
        js.executeScript("arguments[0].dispatchEvent(new Event('change'))", slider2);

        System.out.println("Slider set to MAX");
    }

    @Test(priority = 2)
    public void selectOnlyDrillCheckbox() {
        List<WebElement> checkboxes = driver.findElements(By.xpath("//input[@type='checkbox']"));
        for (WebElement checkbox : checkboxes) {
            if (checkbox.isSelected()) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", checkbox);
                    Thread.sleep(200);
                    checkbox.click();
                } catch (ElementClickInterceptedException e) {
                    System.out.println("Click intercepted, using JavaScript click as fallback.");
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        WebElement drillCheckbox = driver.findElement(By.xpath("html/body/app-root/div/app-overview/div[3]/div[1]/div[3]/ul/div[4]/label/input"));
        if (!drillCheckbox.isSelected()) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", drillCheckbox);
                Thread.sleep(200);
                drillCheckbox.click();
            } catch (ElementClickInterceptedException e) {
                System.out.println("Click intercepted on Drill checkbox, using JavaScript click.");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", drillCheckbox);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("All checkboxes were unchecked, and only the 'Drill' checkbox is selected.");
    }

    @Test(priority = 3)
    public void selectDropdownOption() {
        WebElement dropdown = driver.findElement(By.className("form-select"));
        Select select = new Select(dropdown);
        select.selectByVisibleText("Price (High - Low)");
        System.out.println("Dropdown option 'Price (High - Low)' selected.");

        WebElement selectedOption = select.getFirstSelectedOption();
        String selectedText = selectedOption.getText();
        Assert.assertEquals(selectedText, "Price (High - Low)", "Dropdown selection did not match expected.");
        System.out.println("Assertion passed: Dropdown is set to 'Price (High - Low)'.");
        System.out.println("Dropdown selection test completed.");
    }
    
    @Test(priority = 4)
    public void selectDrillAndSetQuantity() throws InterruptedException {
  //     Thread.sleep(5000);
        // Locate and click the "Drill" item image after all previous tests
        WebElement drillImage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//body[1]/app-root[1]/div[1]/app-overview[1]/div[3]/div[2]/div[1]/a[4]/div[1]/img[1]")));
        drillImage.click();
        System.out.println("Drill item selected.");

        // Pause briefly to ensure page update
        try {
            Thread.sleep(2000); // Pause for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click "+" button to increase quantity 3 times
        WebElement increaseButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-increase-quantity")));
        for (int i = 0; i < 3; i++) {
            increaseButton.click();
            Thread.sleep(500); // Pause between clicks
        }
        
        System.out.println("Quantity increased 3 times to a total of 4.");

        // Verify the quantity
        WebElement quantityInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='number']")));
        int actualQuantity = Integer.parseInt(quantityInput.getAttribute("value"));
        Assert.assertEquals(actualQuantity, 4, "Quantity did not match expected value of 4.");
        System.out.println("Quantity correctly set to 4.");
    }


    @Test(priority = 5)
    public void addToCartAndCheckAlert() {
        try {
            // Click on the "Add to cart" button
            WebElement addToCartButton = driver.findElement(By.id("btn-add-to-cart"));
            addToCartButton.click();
            Thread.sleep(1000); // Short pause for alert to appear

            // Locate the alert element
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='toast-body']")));

            // Capture screenshot if the alert is present
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File("alert_screenshot.png");
            FileUtils.copyFile(screenshot, destinationFile);
            System.out.println("Screenshot taken: alert_screenshot.png");

            // Assert that the alert contains the expected text
            String alertText = alert.getText();
            Assert.assertEquals(alertText, "Oeps, something went wrong.", "Alert text did not match expected.");

            System.out.println("Assertion passed: Alert text matches expected.");

        } catch (Exception e) {
            System.out.println("Alert did not appear as expected or another issue occurred.");
            e.printStackTrace();
        }
    }
 
    @Test(priority = 6)
    public void verifyCartAndProceedToCheckout() throws InterruptedException {
        int quantity = 6; // Change this value to set a different quantity

        // Step 1: Navigate to Cart
        WebElement cartButton = driver.findElement(By.xpath("//a[@aria-label='cart']"));
        cartButton.click();
        System.out.println("Navigated to Cart.");
        Thread.sleep(5000);  // Wait for page load

        // Step 2: Set Quantity using JavaScript and trigger change event
        WebElement quantityField = driver.findElement(By.xpath("//input[@type='number']"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value='" + quantity + "'; arguments[0].dispatchEvent(new Event('change'));", quantityField);
        System.out.println("Quantity set to " + quantity + " and change event triggered.");

        // Step 3: Retrieve Price and Total
        WebElement priceElement = driver.findElement(By.xpath("//*[@data-test='product-price']"));
        WebElement totalElement = driver.findElement(By.xpath("//*[@data-test='cart-total']"));

        String priceText = priceElement.getText().replace("$", ""); // Remove currency symbol
        String totalText = totalElement.getText().replace("$", "");

        double price = Double.parseDouble(priceText);
        double total = Double.parseDouble(totalText);

        // Output to Console for Verification
        System.out.println("Price per item: $" + price);
        System.out.println("Quantity: " + quantity);
        System.out.println("Expected Total (Price * Quantity): $" + (price * quantity));
        System.out.println("Displayed Total: $" + total);

        // Step 4: Assert Total = Price * Quantity
        Assert.assertEquals(total, price * quantity, "Total does not match the expected calculated value.");
        System.out.println("Assertion passed: Total is correctly calculated based on quantity.");

        // Step 5: Pause and Proceed to Checkout
        Thread.sleep(5000);  // Wait before clicking checkout
        WebElement checkoutButton = driver.findElement(By.xpath("//button[normalize-space()='Proceed to checkout']"));
        checkoutButton.click();
        System.out.println("Clicked on Proceed to checkout.");
    }

    
    
    

    @AfterClass
    public void tearDown() {
        try {
            Thread.sleep(5000);  // Wait for observation
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.quit();
    }
}
