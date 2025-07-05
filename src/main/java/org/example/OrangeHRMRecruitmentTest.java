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

        WebElement searchField = driver.findElement(By.xpath("//input[@placeholder='Type for hints...']"));
        searchField.clear();
        searchField.sendKeys("S");
        Thread.sleep(2000);

        // ⬇️ Wait for dropdown option to appear and click it
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement suggestion = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='listbox']//span[contains(text(), 'Sumana Ghimire')]")
        ));
        suggestion.click();

        driver.findElement(By.xpath("//button[text()=' Search ']")).click();
        Thread.sleep(2000);

        List<WebElement> candidates = driver.findElements(By.xpath("//div[@class='oxd-table-card']"));
        Assertions.assertFalse(candidates.isEmpty(), "Candidate search returned no results.");
    }

    @Order(4)
    @Test
    public void addVacanciesTest() throws InterruptedException {
        driver.findElement(By.xpath("//a[@class='oxd-topbar-body-nav-tab-item' and text()='Vacancies']")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//div[@class='orangehrm-header-container']//button[contains(@class, 'oxd-button') and contains(., 'Add')]")).click();
        addVacancy("Test Vacancy 1","QA Engineer", "Orange  Test");

        driver.findElement(By.xpath("//a[@class='oxd-topbar-body-nav-tab-item' and text()='Vacancies']")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//div[@class='orangehrm-header-container']//button[contains(@class, 'oxd-button') and contains(., 'Add')]")).click();
        addVacancy("Test Vacancy 2","Account Assistant", "sww test");
    }

    private void addVacancy(String vacancyName, String jobTitle, String hiringManager) throws InterruptedException {
        // Set Vacancy Name
        driver.findElement(By.xpath("//label[text()='Vacancy Name']/ancestor::div[contains(@class,'oxd-input-group')]/descendant::input")).sendKeys(vacancyName);

        // Open the dropdown
        driver.findElement(By.xpath("//label[text()='Job Title']/following::div[contains(@class,'oxd-select-text')]")).click();
        Thread.sleep(1000); // Wait for options to load

        // Select the job title
        driver.findElement(By.xpath("//div[@role='option' and normalize-space()='" + jobTitle + "']")).click();

        //select Hiring Manager
        WebElement input = driver.findElement(By.xpath("//input[@placeholder='Type for hints...']"));
        input.clear();
        input.sendKeys(hiringManager);

        //set hiring manager
        driver.findElement(By.xpath("//div[@role='option' and contains(text(),'" + hiringManager + "')]")).click();

        // Save
        driver.findElement(By.xpath("//button[text()='Save']")).click();
        Thread.sleep(2000);
    }

    @Order(5)
    @Test
    public void searchVacancyTest() throws InterruptedException {
        driver.findElement(By.xpath("//a[text()='Vacancies']")).click();
        Thread.sleep(1000);
        WebElement managerField = driver.findElement(By.xpath("//input[@placeholder='Type for hints...']"));
        managerField.clear();
        managerField.sendKeys("manda user");
        driver.findElement(By.xpath("//button[text()=' Search ']")).click();
        Thread.sleep(2000);
        List<WebElement> vacancies = driver.findElements(By.xpath("//div[@class='oxd-table-card']"));
        Assertions.assertTrue(vacancies.size() >= 2, "Less than 2 vacancies found.");
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
