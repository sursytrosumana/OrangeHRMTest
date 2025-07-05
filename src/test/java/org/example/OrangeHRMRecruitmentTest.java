package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.pages.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.time.Duration;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrangeHRMRecruitmentTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static LoginPage loginPage;
    private static RecruitmentPage recruitmentPage;
    private static VacanciesPage vacanciesPage;

    private static ExtentReports extent;
    private static ExtentTest test;

    private static final String BASE_URL = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    @BeforeAll
    public static void setup() {
        // Setup ExtentReports
        ExtentSparkReporter spark = new ExtentSparkReporter("target/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);

        // Setup WebDriver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(BASE_URL);

        // Initialize Page Objects
        loginPage = new LoginPage(driver, wait);
        recruitmentPage = new RecruitmentPage(driver, wait);
        vacanciesPage = new VacanciesPage(driver, wait);
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) driver.quit();
        if (extent != null) extent.flush();
    }

    @Order(1)
    @ParameterizedTest
    @MethodSource("org.example.utils.CSVUtils#loadLoginData")
    public void loginTest(String username, String password) {
        test = extent.createTest("Login Test - User: " + username);
        try {
            loginPage.login(username, password, test);
            wait.until(ExpectedConditions.urlContains("dashboard"));
            Assertions.assertTrue(driver.getCurrentUrl().contains("dashboard"), "Login failed or dashboard not reached.");
            test.pass("Login successful for user: " + username);
        } catch (Exception e) {
            test.fail("Login test failed: " + e.getMessage());
            Assertions.fail(e);
        }
    }

    @Order(2)
    @ParameterizedTest
    @MethodSource("org.example.utils.CSVUtils#loadCandidateData")
    public void addCandidateTest(String firstName, String lastName, String email) {
        test = extent.createTest("Add Candidate Test - " + firstName + " " + lastName);
        try {
            recruitmentPage.goToRecruitment(test);
            recruitmentPage.addCandidate(firstName, lastName, email, test);
            test.pass("Candidate added successfully: " + firstName + " " + lastName);
        } catch (Exception e) {
            test.fail("Add Candidate failed: " + e.getMessage());
            Assertions.fail(e);
        }
    }

    @Order(3)
    @ParameterizedTest
    @MethodSource("org.example.utils.CSVUtils#loadCandidateSearchData")
    public void searchCandidateTest(String firstName, String fullName) {
        test = extent.createTest("Search Candidate Test - " + fullName);
        try {
            recruitmentPage.goToRecruitment(test);
            waitUntilVisible("//h5[text()='Candidates']");

            recruitmentPage.typeAndSelectFromDropdown(
                    "//input[@placeholder='Type for hints...']",
                    firstName,
                    fullName,
                    test
            );

            clickWhenClickable("//button[text()=' Search ']");
            waitUntilPresent("//div[@class='oxd-table-card']");

            List<WebElement> results = recruitmentPage.getCandidateSearchResults();
            Assertions.assertFalse(results.isEmpty(), "Candidate search returned no results.");
            test.pass("Search returned results for: " + fullName);
        } catch (Exception e) {
            test.fail("Search Candidate failed: " + e.getMessage());
            Assertions.fail(e);
        }
    }

    @Order(4)
    @ParameterizedTest
    @MethodSource("org.example.utils.CSVUtils#loadVacancyData")
    public void addVacancyTest(String vacancyName, String jobTitle, String hiringManager) {
        test = extent.createTest("Add Vacancy Test - " + vacancyName);
        try {
            recruitmentPage.goToVacancies(test);
            vacanciesPage.addVacancy(vacancyName, jobTitle, hiringManager, test);
            test.pass("Vacancy added: " + vacancyName);
        } catch (Exception e) {
            test.fail("Add Vacancy failed: " + e.getMessage());
            Assertions.fail(e);
        }
    }

    @Order(5)
    @ParameterizedTest
    @MethodSource("org.example.utils.CSVUtils#loadVacancySearchData")
    public void searchVacancyTest(String jobTitle, String expectedVacancy) throws InterruptedException {
        test = extent.createTest("Search Vacancy Test - " + expectedVacancy);
        try {
            recruitmentPage.goToVacancies(test);
            vacanciesPage.selectDropdown("Job Title", jobTitle, test);
            Thread.sleep(1000); // debug
            scrollAndClick("//button[@type='submit' and contains(.,'Search')]");
            waitUntilPresent("//div[@class='oxd-table-card']");

            List<WebElement> vacancies = vacanciesPage.getVacancyResults();
            boolean found = vacancies.stream().anyMatch(v -> v.getText().contains(expectedVacancy));
            Assertions.assertTrue(found, "Missing expected vacancy: " + expectedVacancy);
            test.pass("Vacancy found: " + expectedVacancy);
        } catch (Exception e) {
            test.fail("Search Vacancy failed: " + e.getMessage());
            Assertions.fail(e);
        }
    }

    // Helpers
    private void waitUntilVisible(String xpath) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }

    private void waitUntilPresent(String xpath) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
    }

    private void scrollAndClick(String xpath) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", element);
    }

    private void clickWhenClickable(String xpath) {
        By locator = By.xpath(xpath);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}
