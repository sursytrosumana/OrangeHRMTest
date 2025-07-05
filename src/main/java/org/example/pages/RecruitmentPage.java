package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.util.List;

public class RecruitmentPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Common Locators
    private static final By RECRUITMENT_TAB = By.xpath("//span[text()='Recruitment']");
    private static final By VACANCIES_TAB = By.xpath("//a[text()='Vacancies']");
    private static final By ADD_CANDIDATE_BUTTON = By.xpath("//button[contains(@class, 'oxd-button') and contains(., 'Add')]");
    private static final By FIRST_NAME_INPUT = By.name("firstName");
    private static final By LAST_NAME_INPUT = By.name("lastName");
    private static final By EMAIL_INPUT = By.xpath("(//input[@placeholder='Type here'])[1]");
    private static final By SUBMIT_BUTTON = By.xpath("//button[@type='submit']");
    private static final By DROPDOWN_LISTBOX = By.xpath("//div[@role='listbox']");
    private static final By CANDIDATE_RESULT_CARD = By.xpath("//div[@class='oxd-table-card']");

    public RecruitmentPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // Navigation
    public void goToRecruitment() {
        waitUntilClickable(RECRUITMENT_TAB).click();
    }

    public void goToVacancies() {
        goToRecruitment();
        waitUntilClickable(VACANCIES_TAB).click();
    }

    // Candidate Actions
    public void addCandidate(String firstName, String lastName, String email) {
        waitUntilClickable(ADD_CANDIDATE_BUTTON).click();
        waitUntilVisible(FIRST_NAME_INPUT).sendKeys(firstName);
        waitUntilVisible(LAST_NAME_INPUT).sendKeys(lastName);
        waitUntilVisible(EMAIL_INPUT).sendKeys(email);
        waitUntilClickable(SUBMIT_BUTTON).click();
    }

    public List<WebElement> getCandidateSearchResults() {
        return driver.findElements(CANDIDATE_RESULT_CARD);
    }

    // Dynamic Dropdown Interaction
    public void typeAndSelectFromDropdown(String inputXpath, String typedValue, String visibleOptionText) {
        WebElement input = waitUntilClickable(By.xpath(inputXpath));
        input.clear();
        input.sendKeys(typedValue);

        waitUntilVisible(DROPDOWN_LISTBOX);

        List<String> fallbackXPaths = List.of(
                "//div[@role='listbox']//div[normalize-space(text())='" + visibleOptionText + "']",
                "//div[@role='listbox']//div[contains(text(),'" + visibleOptionText + "')]",
                "//div[@role='listbox']//span[normalize-space(text())='" + visibleOptionText + "']",
                "//div[@role='listbox']//*[contains(text(),'" + visibleOptionText + "')]"
        );

        for (String xpath : fallbackXPaths) {
            try {
                WebElement option = waitUntilClickable(By.xpath(xpath));
                scrollIntoView(option);
                option.click();
                return;
            } catch (TimeoutException ignored) {
                // Try next xpath
            }
        }

        throw new NoSuchElementException("Dropdown option not found: " + visibleOptionText);
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
