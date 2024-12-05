package MavenProject.testingandlearning;




import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class LoginTest {

    WebDriver driver;

    @BeforeClass
    public void setUp() {
        // Use WebDriverManager to handle ChromeDriver setup
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
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

        Thread.sleep(2000);  // pause for observation
        
        
        
        // Move the max slider by 100 pixels
       // action.dragAndDropBy(slider2, 477, 0).perform();
      //  Thread.sleep(2000);  // wait for effect
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