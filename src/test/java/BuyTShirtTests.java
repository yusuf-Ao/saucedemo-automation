import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BuyTShirtTests {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static Map<String, String> productDetails;
    private static final String SAUCE_DEMO_URL = "https://www.saucedemo.com/";

    @BeforeAll
    public static void setUp() {
        productDetails  = new HashMap<>();
        // TODO -> Uncomment the below line if your system has no chrome browser installed as default.
        //System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        // Open Chrome in Incognito mode
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.get(SAUCE_DEMO_URL); // Precondition: Navigate to the login page
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @Test
    @Order(1)
    public void loginAndVerifyUserIsDirectedToTheProductsPage() {
        // Step 2: Enter valid credentials to log in
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
        // Step 3: Verify login success and product page is shown
        assertTrue(driver.getCurrentUrl().contains("inventory.html"));
    }

    @Test
    @Order(2)
    public void selectTShirtByNameAndVerifyThatTheTShirtDetailsPageIsDisplayed()  {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#inventory_container > div > div:nth-child(1)")));
        WebElement firstProductContainer = driver.findElement(By.cssSelector("#inventory_container > div > div:nth-child(1)"));
        WebElement firstProductName = firstProductContainer.findElement(By.className("inventory_item_name"));

        productDetails.put("name", firstProductContainer.findElement(By.className("inventory_item_name")).getText());
        productDetails.put("desc",firstProductContainer.findElement(By.className("inventory_item_desc")).getText());
        productDetails.put("price",firstProductContainer.findElement(By.className("inventory_item_price")).getText());

        // Step 4: Select a T-shirt by Name
        firstProductName.click();

        // Step 5: Verify T-shirt details page
        assertTrue(driver.findElement(By.xpath("//button[text()='Back to products']")).isDisplayed());
    }

    @Test
    @Order(3)
    public void clickOnAddToCartButtonAndVerifyThatTheTShirtIsAddedToCartSuccessfully() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[text()='Add to cart']")));
        WebElement addToCartButton = driver.findElement(By.xpath("//button[text()='Add to cart']"));
        // Step 6: Add T-shirt to cart
        addToCartButton.click();
        // Step 7: Verify T-shirt added to cart
        assertTrue(driver.findElement(By.xpath("//button[text()='Remove']")).isDisplayed());
    }

    @Test
    @Order(4)
    public void navigateToCartAndVerifyThatTheCartPageIsDisplayed() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("shopping_cart_link")));
        // Step 8: Navigate to cart
        driver.findElement(By.className("shopping_cart_link")).click();
        // Step 9: Verify cart page is displayed
        assertTrue(driver.getCurrentUrl().contains("cart.html"));
    }

    @Test
    @Order(5)
    public void reviewTheItemsInCartAndVerifyThatTheSelectedTShirtDetailsIsDisplayedCorrectly() {
        //fetch all items on cart
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("cart_item")));
        List<WebElement> cartItems = driver.findElements(By.className("cart_item"));
        boolean found = false;
        for (WebElement item: cartItems) {
            String name = item.findElement(By.className("inventory_item_name")).getText();
            String desc = item.findElement(By.className("inventory_item_desc")).getText();
            String price = item.findElement(By.className("inventory_item_price")).getText();
            // Step 10: Verify that cart contains and matches the selected TShirt
            if (productDetails.get("name").equals(name)
                    && productDetails.get("desc").equals(desc)
                    && productDetails.get("price").equals(price)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    @Order(6)
    public void clickTheCheckoutButtonAndVerifyThatTheCheckoutInfoPageIsDisplayed() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("checkout")));
        // Step 11: Click on the Checkout button
        driver.findElement(By.id("checkout")).click();
        // Step 12: Verify checkout info page
        assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"));
    }

    @Test
    @Order(7)
    public void enterTheRequiredCheckoutInformationAndClickOnContinueButton() {
        // Step 13: Enter checkout information
        driver.findElement(By.id("first-name")).sendKeys("Yusuf");
        driver.findElement(By.id("last-name")).sendKeys("Ahmed");
        driver.findElement(By.id("postal-code")).sendKeys("12345");
        // Step 14: Continue to next step
        driver.findElement(By.id("continue")).click();
        assertTrue(driver.getCurrentUrl().contains("checkout-step-two.html"));
    }

    @Test
    @Order(8)
    public void verifyThatTheOrderSummaryPageIsDisplayedShowingTheTShirtDetailsAndTotalAmount() {
        // Step 15: Verify order summary page is displayed
        assertTrue(driver.getCurrentUrl().contains("checkout-step-two.html"));
        assertTrue(driver.findElement(By.className("cart_item")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("#checkout_summary_container > div > div.summary_info > div.summary_info_label.summary_total_label")).isDisplayed());
    }

    @Test
    @Order(9)
    public void clickTheFinishButtonAndVerifyThatTheOrderConfirmationPageIsDisplayed() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("finish")));
        // Step 16: Click the finish button
        driver.findElement(By.id("finish")).click();
        // Step 17: Verify order confirmation page is displayed
        assertTrue(driver.getCurrentUrl().contains("checkout-complete.html"));
    }

    @Test
    @Order(10)
    public void logoutAndVerifyThatUserIsRedirectedToLoginPage() throws InterruptedException {
        // Step 18: Logout
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("react-burger-menu-btn")));
        driver.findElement(By.id("react-burger-menu-btn")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("logout_sidebar_link")));
        driver.findElement(By.id("logout_sidebar_link")).click();

        // Step 19: Verify logout
        assertEquals(SAUCE_DEMO_URL, driver.getCurrentUrl());
    }

    @AfterAll
    public static void tearDown() {
        driver.quit(); // Close the browser
    }

}
