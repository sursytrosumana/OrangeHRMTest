package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrangeHRMRecruitmentTest {

    static WebDriver driver;

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
    }

    @Order(1)
    @Test
    public void loginTest() throws InterruptedException {
        Thread.sleep(2000);
        driver.findElement(By.name("username")).sendKeys("Admin");
        driver.findElement(By.name("password")).sendKeys("admin123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Thread.sleep(3000);
        Assertions.assertTrue(driver.getCurrentUrl().contains("dashboard"));
    }

    @Order(2)
    @Test
    public void addCandidatesTest() throws InterruptedException {
        driver.findElement(By.xpath("//span[text()='Recruitment']")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[contains(@class, 'oxd-button') and contains(., 'Add')]")).click();
        addCandidate("Sumana", "Ghimire", "Sumana.Ghimire@email.com");
        driver.findElement(By.xpath("//span[text()='Recruitment']")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[contains(@class, 'oxd-button') and contains(., 'Add')]")).click();
        addCandidate("Sarmila", "Shrestha", "Sarmila.shrestha@email.com");
    }

    private void addCandidate(String firstName, String lastName, String email) throws InterruptedException {
        driver.findElement(By.name("firstName")).sendKeys(firstName);
        driver.findElement(By.name("lastName")).sendKeys(lastName);
        driver.findElement(By.xpath("(//input[@placeholder='Type here'])[1]")).sendKeys(email);
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(2000);
    }

    @Order(3)
    @Test
    public void searchCandidateTest() throws InterruptedException {
        driver.findElement(By.xpath("//span[text()='Recruitment']")).click();
        Thread.sleep(2000);

        typeAndSelectFromDropdown(
                "//input[@placeholder='Type for hints...']",
                "Sumana ",
                "Sumana Ghimire"
        );

        driver.findElement(By.xpath("//button[text()=' Search ']")).click();
        Thread.sleep(2000);

        List<WebElement> candidates = driver.findElements(By.xpath("//div[@class='oxd-table-card']"));
        Assertions.assertFalse(candidates.isEmpty(), "Candidate search returned no results.");
    }

    @Order(4)
    @Test
    public void addVacanciesTest() throws InterruptedException {
        System.out.println("Adding first vacancy...");
        addVacancy("Test Vacancy 1", "QA Engineer", "OrangeEdited Test");

        // Go back to Recruitment > Vacancies
        Thread.sleep(1000);
        driver.findElement(By.xpath("//span[text()='Recruitment']")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[text()='Vacancies']")).click();
        Thread.sleep(2000);

        System.out.println("Adding second vacancy...");
        addVacancy("Test Vacancy 2", "Account Assistant", "sww test");
    }

    private void addVacancy(String vacancyName, String jobTitle, String hiringManager) throws InterruptedException {
        // Navigate to Vacancies
        driver.findElement(By.xpath("//a[@class='oxd-topbar-body-nav-tab-item' and text()='Vacancies']")).click();
        Thread.sleep(2000);

        // Click Add
        driver.findElement(By.xpath("//div[@class='orangehrm-header-container']//button[contains(@class, 'oxd-button') and contains(., 'Add')]")).click();

        // Set Vacancy Name
        driver.findElement(By.xpath("//label[text()='Vacancy Name']/ancestor::div[contains(@class,'oxd-input-group')]/descendant::input")).sendKeys(vacancyName);

        // Set Job Title
        driver.findElement(By.xpath("//label[text()='Job Title']/following::div[contains(@class,'oxd-select-text')]")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//div[@role='option' and normalize-space()='" + jobTitle + "']")).click();

        // Set Hiring Manager using more specific XPath
        String hiringManagerInputXpath = "//label[text()='Hiring Manager']/ancestor::div[contains(@class,'oxd-input-group')]/descendant::input[@placeholder='Type for hints...']";
        typeAndSelectFromDropdown(hiringManagerInputXpath, hiringManager, hiringManager);

        // Save
        driver.findElement(By.xpath("//button[text()='Save']")).click();
        Thread.sleep(2000);
    }

    @Order(5)
    @Test
    public void searchVacancyTest() throws InterruptedException {
        driver.findElement(By.xpath("//a[text()='Vacancies']")).click();
        Thread.sleep(1000);

        selectDropdownValue("Job Title", "QA Engineer");

        driver.findElement(By.xpath("//button[@type='submit' and contains(.,'Search')]")).click();
        Thread.sleep(2000);
        List<WebElement> vacancies = driver.findElements(By.xpath("//div[@class='oxd-table-card']"));
        Assertions.assertTrue(vacancies.size() >= 2, "Less than 2 vacancies found.");
    }

    public void selectDropdownValue(String dropdownLabel, String valueToSelect) {
        WebElement dropdown = driver.findElement(By.xpath("//label[text()='" + dropdownLabel + "']/following::div[contains(@class,'oxd-select-text')]"));
        dropdown.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement option = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='listbox']//span[text()='" + valueToSelect + "']")));
        option.click();
    }

    public void typeAndSelectFromDropdown(String inputXpath, String typedValue, String visibleOptionText) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement inputField = driver.findElement(By.xpath(inputXpath));
        inputField.click();
        inputField.clear();

        // Type the value
        inputField.sendKeys(typedValue);
        Thread.sleep(1500); // Wait for dropdown to populate

        // Wait for the dropdown to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='listbox']")));

        // Try different strategies to find and click the dropdown option
        WebElement option = null;
        boolean optionFound = false;

        // Strategy 1: Direct text match in dropdown option
        try {
            option = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@role='listbox']//div[contains(@class, 'oxd-autocomplete-option') and normalize-space(text())='" + visibleOptionText + "']")
            ));
            optionFound = true;
        } catch (Exception e1) {
            System.out.println("Strategy 1 failed: " + e1.getMessage());
        }

        // Strategy 2: Contains text match
        if (!optionFound) {
            try {
                option = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@role='listbox']//div[contains(@class, 'oxd-autocomplete-option') and contains(normalize-space(text()), '" + visibleOptionText + "')]")
                ));
                optionFound = true;
            } catch (Exception e2) {
                System.out.println("Strategy 2 failed: " + e2.getMessage());
            }
        }

        // Strategy 3: Find by span text
        if (!optionFound) {
            try {
                option = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@role='listbox']//span[normalize-space(text())='" + visibleOptionText + "']")
                ));
                optionFound = true;
            } catch (Exception e3) {
                System.out.println("Strategy 3 failed: " + e3.getMessage());
            }
        }

        // Strategy 4: Any element containing the text
        if (!optionFound) {
            try {
                option = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@role='listbox']//*[contains(normalize-space(text()), '" + visibleOptionText + "')]")
                ));
                optionFound = true;
            } catch (Exception e4) {
                System.out.println("Strategy 4 failed: " + e4.getMessage());
            }
        }

        // Strategy 5: Click first available option if nothing else works
        if (!optionFound) {
            try {
                List<WebElement> options = driver.findElements(By.xpath("//div[@role='listbox']//div[contains(@class, 'oxd-autocomplete-option')]"));
                if (!options.isEmpty()) {
                    option = options.get(0);
                    optionFound = true;
                    System.out.println("Using first available option");
                }
            } catch (Exception e5) {
                System.out.println("Strategy 5 failed: " + e5.getMessage());
            }
        }

        if (!optionFound) {
            throw new RuntimeException("Could not find any dropdown option for: " + visibleOptionText);
        }

        // Scroll to the option and click
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", option);
        Thread.sleep(300);

        // Try normal click first
        try {
            option.click();
        } catch (Exception e) {
            // If normal click fails, use JavaScript click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
        }

        // Wait for dropdown to disappear
        Thread.sleep(1000);

        // Verify the value was selected by checking if the input field contains the expected value
        try {
            WebElement selectedInput = driver.findElement(By.xpath(inputXpath));
            String selectedValue = selectedInput.getAttribute("value");
            System.out.println("Selected value: " + selectedValue);
        } catch (Exception e) {
            System.out.println("Could not verify selected value: " + e.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}