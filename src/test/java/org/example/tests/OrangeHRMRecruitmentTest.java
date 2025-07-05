package org.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.pages.*;
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

    private static LoginPage loginPage;
    private static DashboardPage dashboardPage;
    private static RecruitmentPage recruitmentPage;
    private static VacanciesPage vacanciesPage;

    private static final String BASE_URL = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(BASE_URL);

        // Instantiate page objects
        loginPage = new LoginPage(driver, wait);
        dashboardPage = new DashboardPage(driver, wait);
        recruitmentPage = new RecruitmentPage(driver, wait);
        vacanciesPage = new VacanciesPage(driver, wait);
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) driver.quit();
    }

    @Order(1)
    @Test
    public void loginTest() {
        loginPage.login("Admin", "admin123");
        wait.until(ExpectedConditions.urlContains("dashboard"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("dashboard"));
    }

    @Order(2)
    @Test
    public void addCandidatesTest() {
        recruitmentPage.goToRecruitment();
        recruitmentPage.addCandidate("Sumana", "Ghimire", "Sumana.Ghimire@email.com");

        recruitmentPage.goToRecruitment();
        recruitmentPage.addCandidate("Sarmila", "Shrestha", "Sarmila.shrestha@email.com");
    }

    @Order(3)
    @Test
    public void searchCandidateTest() {
        recruitmentPage.goToRecruitment();

        // 🔒 Wait until the page heading or input becomes visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h5[text()='Candidates']")
        ));

        // 🔁 Try selecting Sumana Ghimire from dropdown
        recruitmentPage.typeAndSelectFromDropdown(
                "//input[@placeholder='Type for hints...']",
                "Sumana",
                "Sumana Ghimire"
        );

        // 🔍 Click Search
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()=' Search ']")
        )).click();

        // ⏳ Wait for results
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[@class='oxd-table-card']")
        ));

        List<WebElement> results = recruitmentPage.getCandidateSearchResults();
        System.out.println("Found " + results.size() + " search result(s).");
        Assertions.assertFalse(results.isEmpty(), "Candidate search returned no results.");
    }



    @Order(4)
    @Test
    public void addVacanciesTest() {
        recruitmentPage.goToVacancies(); // Initial nav to Vacancies page

        vacanciesPage.addVacancy("Test Vacancy 1", "Manual Tester", "Orange Test");

        recruitmentPage.goToVacancies(); // Return to list view to re-enable +Add

        vacanciesPage.addVacancy("Test Vacancy 2", "Test Lead", "sww test");
    }


    @Order(5)
    @Test
    public void searchVacancyTest() {
        recruitmentPage.goToVacancies();
        vacanciesPage.selectDropdown("Job Title", "Manual Tester");

        // 🧠 Search
        WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(.,'Search')]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchBtn);

        // ⏳ Wait for table to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[@class='oxd-table-card']")
        ));

        List<WebElement> vacancies = vacanciesPage.getVacancyResults();
        System.out.println("Found " + vacancies.size() + " total vacancy card(s):");

        boolean foundVacancy1 = false;
        boolean foundVacancy2 = false;

        for (WebElement card : vacancies) {
            String text = card.getText();
            System.out.println("→ " + text);

            if (text.contains("Test Vacancy 1")) {
                foundVacancy1 = true;
            }
            if (text.contains("Test Vacancy 2")) {
                foundVacancy2 = true;
            }
        }

        Assertions.assertTrue(foundVacancy1, "Did not find 'Test Vacancy 1' in search results.");
        Assertions.assertTrue(foundVacancy2, "Did not find 'Test Vacancy 2' in search results.");
    }

}
