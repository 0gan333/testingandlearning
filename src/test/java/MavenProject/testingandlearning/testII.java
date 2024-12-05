package MavenProject.testingandlearning;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;


public class testII {
    WebDriver driver;
    WebDriverWait wait;
    @BeforeClass
    
    
    public void setup() {
    	 WebDriverManager.chromedriver().setup();
         driver = new ChromeDriver();
         driver.manage().window().maximize();
         wait = new WebDriverWait(driver, Duration.ofSeconds(10));
         
         
    }
         
    @Test(priority = 1)
    public void checkTitle() throws InterruptedException {
        driver.get("https://with-bugs.practicesoftwaretesting.com/#/");

        String expectedTitle = "Practice Software Testing - Toolshop - v5.0 with bugs";
        String actualTitle = driver.getTitle();
        Assert.assertEquals(actualTitle, expectedTitle, "The title of the page is not as expected.");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement slider = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span[aria-label='ngx-slider']")));
        WebElement slider2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@aria-label='ngx-slider-max']")));

        Actions action = new Actions(driver);

        // Move slider by 50 pixels
        action.dragAndDropBy(slider, 50, 0).perform();
        Thread.sleep(2000);  // wait for effect

        // Second part: Move the slider to its maximum value
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.left = '100%'", slider2); // Move the slider to the end
        js.executeScript("arguments[0].dispatchEvent(new Event('change'))", slider2); // Trigger the change event

        System.out.println("Slider set to MAX");

        
        Thread.sleep(2000);  // pause for observation
        
        
    }
    @Test(priority =2)
    public void selectOnlyDrillCheckbox() {
        // Locate all checkboxes on the page
        List<WebElement> checkboxes = driver.findElements(By.xpath("//input[@type='checkbox']"));
        
        // Uncheck all checkboxes
        for (WebElement checkbox : checkboxes) {
            if (checkbox.isSelected()) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", checkbox); // Scroll into view
                    Thread.sleep(200); // Add a short pause for stability
                    checkbox.click(); // Attempt to uncheck
                } catch (ElementClickInterceptedException e) {
                    System.out.println("Click intercepted, using JavaScript click as fallback.");
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
       
        // Find and check only the "Drill" checkbox
        WebElement drillCheckbox = driver.findElement(By.xpath("html/body/app-root/div/app-overview/div[3]/div[1]/div[3]/ul/div[4]/label/input")); // Update XPath for "Drill"
        if (!drillCheckbox.isSelected()) { // Ensure "Drill" is not already selected
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", drillCheckbox); // Scroll into view
                Thread.sleep(200); // Short wait for stability
                drillCheckbox.click(); // Select "Drill"
            } catch (ElementClickInterceptedException e) {
                System.out.println("Click intercepted on Drill checkbox, using JavaScript click.");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", drillCheckbox);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("All checkboxes were unchecked, and only the 'Drill' checkbox is selected.");
        }

      //  System.out.println("All checkboxes were unchecked, and only the 'Drill' checkbox is selected.");
    }

        
    @Test(priority =3)
    public void selectDropdownOption() {
        // Open the webpage
        driver.get("https://with-bugs.practicesoftwaretesting.com/#/");
        System.out.println("Page opened successfully.");

        // Locate the dropdown element and select "Price (High - Low)"
        WebElement dropdown = driver.findElement(By.className("form-select"));
        Select select = new Select(dropdown);
        select.selectByVisibleText("Price (High - Low)");
        System.out.println("Dropdown option 'Price (High - Low)' selected.");

        // Assert selected option
        WebElement selectedOption = select.getFirstSelectedOption();
        String selectedText = selectedOption.getText();
        Assert.assertEquals(selectedText, "Price (High - Low)", "Dropdown selection did not match expected.");
        System.out.println("Assertion passed: Dropdown is set to 'Price (High - Low)'.");

        // Print end message
        System.out.println("Dropdown selection test completed.");
    } 
        
        

    @AfterClass
    public void tearDown() {
        // Close the browser
    	  // Pause for 5 seconds before closing the browser
        try {
            Thread.sleep(5000);  // 5000 milliseconds = 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
    	driver.quit();
    }
}





        
        
        
        
        
        
        
        