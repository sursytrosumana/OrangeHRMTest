package org.example.tests;

import org.example.pages.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrangeHRMRecruitmentTest {

    private static WebDriver driver;
    private static LoginPage loginPage;
    private static RecruitmentPage recruitmentPage;
    private static VacancyPage vacancyPage;

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");

        loginPage = new LoginPage(driver);
        recruitmentPage = new RecruitmentPage(driver);
        vacancyPage = new VacancyPage(driver);
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) driver.quit();
    }

    @Test
    @Order(1)
    public void loginTest() {
        loginPage.login("Admin", "admin123");
        Assertions.assertTrue(driver.getCurrentUrl().contains("dashboard"));
    }

    @Test
    @Order(2)
    public void addCandidatesTest() {
        recruitmentPage.navigateTo();
        recruitmentPage.addCandidate("Sumana", "Ghimire", "Sumana.Ghimire@email.com");

        recruitmentPage.navigateTo();
        recruitmentPage.addCandidate("Sarmila", "Shrestha", "Sarmila.shrestha@email.com");
    }

    @Test
    @Order(3)
    public void searchCandidateTest() {
        recruitmentPage.navigateTo();
        recruitmentPage.searchCandidate("Sumana", "Sumana Ghimire");
    }

    @Test
    @Order(4)
    public void addVacancyTest() {
        vacancyPage.navigateTo();
        vacancyPage.addVacancy("Test Vacancy 1", "QA Engineer", "OrangeEdited Test");

        vacancyPage.navigateTo();
        vacancyPage.addVacancy("Test Vacancy 2", "Account Assistant", "sww test");
    }

    @Test
    @Order(5)
    public void searchVacancyTest() {
        vacancyPage.navigateTo();
        vacancyPage.searchVacancy("QA Engineer");
    }
}
