package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrangeHRMRecruitmentTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE_URL = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(BASE_URL);
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) driver.quit();
    }

    @Order(1)
    @Test
    public void loginTest() {
        waitAndSend(By.name("username"), "Admin");
        waitAndSend(By.name("password"), "admin123");
        click(By.cssSelector("button[type='submit']"));

        wait.until(ExpectedConditions.urlContains("dashboard"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("dashboard"));
    }

    @Order(2)
    @Test
    public void addCandidatesTest() {
        navigateToRecruitment();
        addCandidate("Sumana", "Ghimire", "Sumana.Ghimire@email.com");

        navigateToRecruitment();
        addCandidate("Sarmila", "Shrestha", "Sarmila.shrestha@email.com");
    }

    @Order(3)
    @Test
    public void searchCandidateTest() {
        navigateToRecruitment();
        typeAndSelectFromDropdown("//input[@placeholder='Type for hints...']", "Sumana", "Sumana Ghimire");

        click(By.xpath("//button[text()=' Search ']"));

        List<WebElement> results = driver.findElements(By.xpath("//div[@class='oxd-table-card']"));
        Assertions.assertFalse(results.isEmpty(), "Candidate search returned no results.");
    }

    @Order(4)
    @Test
    public void addVacanciesTest() {
        navigateToVacancies();
        addVacancy("Test Vacancy 1", "QA Engineer", "OrangeEdited Test");

        navigateToVacancies();
        addVacancy("Test Vacancy 2", "Account Assistant", "sww test");
    }

    @Order(5)
    @Test
    public void searchVacancyTest() {
        navigateToVacancies();
        selectDropdown("Job Title", "QA Engineer");

        click(By.xpath("//button[@type='submit' and contains(.,'Search')]"));

        List<WebElement> vacancies = driver.findElements(By.xpath("//div[@class='oxd-table-card']"));
        Assertions.assertTrue(vacancies.size() >= 2, "Less than 2 vacancies found.");
    }

    // ------------------- Utility Methods -------------------

    private void navigateToRecruitment() {
        click(By.xpath("//span[text()='Recruitment']"));
    }

    private void navigateToVacancies() {
        navigateToRecruitment();
        click(By.xpath("//a[text()='Vacancies']"));
    }

    private void addCandidate(String firstName, String lastName, String email) {
        click(By.xpath("//button[contains(@class, 'oxd-button') and contains(., 'Add')]"));
        waitAndSend(By.name("firstName"), firstName);
        waitAndSend(By.name("lastName"), lastName);
        waitAndSend(By.xpath("(//input[@placeholder='Type here'])[1]"), email);
        click(By.xpath("//button[@type='submit']"));
    }

    private void addVacancy(String vacancyName, String jobTitle, String hiringManager) {
        click(By.xpath("//div[contains(@class,'orangehrm-header-container')]//button[contains(., 'Add')]"));
        waitAndSend(By.xpath("//label[text()='Vacancy Name']/following::input[1]"), vacancyName);
        selectDropdown("Job Title", jobTitle);
        typeAndSelectFromDropdown("//label[text()='Hiring Manager']/following::input[1]", hiringManager, hiringManager);
        click(By.xpath("//button[text()='Save']"));
    }

    private void selectDropdown(String label, String valueToSelect) {
        By dropdownTrigger = By.xpath("//label[text()='" + label + "']/following::div[contains(@class,'oxd-select-text')]");
        click(dropdownTrigger);

        By optionLocator = By.xpath("//div[@role='listbox']//span[text()='" + valueToSelect + "']");
        wait.until(ExpectedConditions.elementToBeClickable(optionLocator)).click();
    }

    private void typeAndSelectFromDropdown(String inputXpath, String typedValue, String visibleOptionText) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(inputXpath)));
        input.clear();
        input.sendKeys(typedValue);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='listbox']")));

        List<String> xpathsToTry = List.of(
                "//div[@role='listbox']//div[normalize-space(text())='" + visibleOptionText + "']",
                "//div[@role='listbox']//div[contains(text(),'" + visibleOptionText + "')]",
                "//div[@role='listbox']//span[normalize-space(text())='" + visibleOptionText + "']",
                "//div[@role='listbox']//*[contains(text(),'" + visibleOptionText + "')]"
        );

        for (String xpath : xpathsToTry) {
            try {
                WebElement option = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", option);
                option.click();
                return;
            } catch (TimeoutException ignored) {}
        }

        throw new NoSuchElementException("Dropdown option not found: " + visibleOptionText);
    }

    private void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void waitAndSend(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }
}
