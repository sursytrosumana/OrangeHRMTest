package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.pages.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrangeHRMRecruitmentTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static LoginPage loginPage;
    private static RecruitmentPage recruitmentPage;
    private static VacanciesPage vacanciesPage;

    private static final String BASE_URL = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(BASE_URL);

        loginPage = new LoginPage(driver, wait);
        recruitmentPage = new RecruitmentPage(driver, wait);
        vacanciesPage = new VacanciesPage(driver, wait);
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) driver.quit();
    }

    @Order(1)
    @ParameterizedTest
    @MethodSource("org.example.utils.CSVUtils#loadLoginData")
    public void loginTest(String username, String password) {
        loginPage.login(username, password);
        wait.until(ExpectedConditions.urlContains("dashboard"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("dashboard"), "Login failed or dashboard not reached.");
    }

    @Order(2)
    @ParameterizedTest
    @MethodSource("org.example.utils.CSVUtils#loadCandidateData")
    public void addCandidateTest(String firstName, String lastName, String email) {
        recruitmentPage.goToRecruitment();
        recruitmentPage.addCandidate(firstName, lastName, email);
    }

    @Order(3)
    @ParameterizedTest
    @MethodSource("org.example.utils.CSVUtils#loadCandidateSearchData")
    public void searchCandidateTest(String firstName, String fullName) {
        recruitmentPage.goToRecruitment();
        waitUntilVisible("//h5[text()='Candidates']");

        recruitmentPage.typeAndSelectFromDropdown(
                "//input[@placeholder='Type for hints...']",
                firstName,
                fullName
        );

        clickWhenClickable("//button[text()=' Search ']");
        waitUntilPresent("//div[@class='oxd-table-card']");

        List<WebElement> results = recruitmentPage.getCandidateSearchResults();
        Assertions.assertFalse(results.isEmpty(), "Candidate search returned no results.");
    }


    @Order(4)
    @ParameterizedTest
    @MethodSource("org.example.utils.CSVUtils#loadVacancyData")
    public void addVacancyTest(String vacancyName, String jobTitle, String hiringManager) {
        recruitmentPage.goToVacancies();
        vacanciesPage.addVacancy(vacancyName, jobTitle, hiringManager);
    }

    @Order(5)
    @ParameterizedTest
    @MethodSource("org.example.utils.CSVUtils#loadVacancySearchData")
    public void searchVacancyTest(String jobTitle, String expectedVacancy) throws InterruptedException {
        recruitmentPage.goToVacancies();
        vacanciesPage.selectDropdown("Job Title", jobTitle);
        Thread.sleep(1000); // temp, for debug
        scrollAndClick("//button[@type='submit' and contains(.,'Search')]");
        waitUntilPresent("//div[@class='oxd-table-card']");

        List<WebElement> vacancies = vacanciesPage.getVacancyResults();
        boolean found = vacancies.stream().anyMatch(v -> v.getText().contains(expectedVacancy));

        Assertions.assertTrue(found, "Missing expected vacancy: " + expectedVacancy);
    }

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
