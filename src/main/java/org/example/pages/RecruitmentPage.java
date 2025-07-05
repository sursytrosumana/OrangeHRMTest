package org.example.pages;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.util.List;

public class RecruitmentPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Common Locators
    By recruitmentTab = By.xpath("//span[text()='Recruitment']");
    By vacanciesTab = By.xpath("//a[text()='Vacancies']");
    By addCandidateTab = By.xpath("//button[contains(@class, 'oxd-button') and contains(., 'Add')]");
    By inputFirstName = By.name("firstName");
    By inputLastName = By.name("lastName");
    By inputEmail = By.xpath("(//input[@placeholder='Type here'])[1]");
    By save = By.xpath("//button[@type='submit']");
    By candidateNameDropdwon = By.xpath("//div[@role='listbox']");
    By searchResultCandidate = By.xpath("//div[@class='oxd-table-card']");

    public RecruitmentPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // Navigation
    public void goToRecruitment() {
        waitUntilClickable(recruitmentTab).click();
    }

    public void goToRecruitment(ExtentTest test) {
        try {
            test.info("Clicking on Recruitment tab");
            goToRecruitment();
            test.pass("Recruitment page loaded");
        } catch (Exception e) {
            test.fail("Failed to navigate to Recruitment: " + e.getMessage());
            throw e;
        }
    }

    public void goToVacancies() {
        goToRecruitment();
        waitUntilClickable(vacanciesTab).click();
    }

    public void goToVacancies(ExtentTest test) {
        try {
            test.info("Navigating to Vacancies page via Recruitment tab");
            goToVacancies();
            test.pass("Vacancies page loaded");
        } catch (Exception e) {
            test.fail("Failed to go to Vacancies: " + e.getMessage());
            throw e;
        }
    }


    public void addCandidate(String firstName, String lastName, String email, ExtentTest test) {
        try {
            test.info("Clicking Add button");
            waitUntilClickable(addCandidateTab).click();

            test.info("Filling candidate form: " + firstName + " " + lastName + ", " + email);
            waitUntilVisible(inputFirstName).sendKeys(firstName);
            waitUntilVisible(inputLastName).sendKeys(lastName);
            waitUntilVisible(inputEmail).sendKeys(email);

            test.info("Submitting candidate form");
            waitUntilClickable(save).click();

            test.pass("Candidate added successfully");
        } catch (Exception e) {
            test.fail("Add Candidate failed: " + e.getMessage());
            throw e;
        }
    }

    //returns search results of candidate results
    public List<WebElement> getCandidateSearchResults() {
        return driver.findElements(searchResultCandidate);
    }

    public void typeAndSelectFromDropdown(String inputXpath, String typedValue, String visibleOptionText, ExtentTest test) {
        try {
            test.info("Typing into dropdown input: " + typedValue);
            WebElement input = waitUntilClickable(By.xpath(inputXpath));
            input.clear();
            input.sendKeys(typedValue);
            waitUntilVisible(candidateNameDropdwon);
            String dropdownPath = "//div[@role='listbox']//span[normalize-space(text())='" + visibleOptionText + "']";

            WebElement option = waitUntilClickable(By.xpath(dropdownPath));
            scrollIntoView(option);
            option.click();
            test.pass("Selected dropdown option: " + visibleOptionText);
        } catch (TimeoutException ignored) {


            throw new NoSuchElementException("Dropdown option not found: " + visibleOptionText);
        } catch (Exception e) {
            test.fail("Dropdown selection failed: " + e.getMessage());
            throw e;
        }
    }

    // Helpers
    private WebElement waitUntilClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private WebElement waitUntilVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}
