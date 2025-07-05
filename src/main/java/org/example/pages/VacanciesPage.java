package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.util.List;

public class VacanciesPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private static final By ADD_VACANCY_BUTTON = By.xpath(
            "//div[contains(@class,'orangehrm-header-container')]//button[contains(., 'Add')]"
    );
    private static final By VACANCY_NAME_INPUT = By.xpath(
            "//label[text()='Vacancy Name']/following::input[1]"
    );
    private static final By SAVE_BUTTON = By.xpath(
            "//button[@type='submit' and normalize-space()='Save']"
    );
    private static final By VACANCY_RESULTS = By.xpath(
            "//div[@class='oxd-table-card']"
    );

    public VacanciesPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    // ---------- Public Actions ----------

    public void addVacancy(String vacancyName, String jobTitle, String hiringManager) {
        click(ADD_VACANCY_BUTTON);
        type(VACANCY_NAME_INPUT, vacancyName);
        selectDropdown("Job Title", jobTitle);
        typeAndSelectFromDropdown(
                "//label[text()='Hiring Manager']/following::input[1]",
                hiringManager,
                hiringManager
        );
        click(SAVE_BUTTON);
    }

    public void selectDropdown(String label, String visibleText) {
        By trigger = By.xpath("//label[text()='" + label + "']/following::div[contains(@class,'oxd-select-text')]");
        By option = By.xpath("//div[@role='listbox']//span[text()='" + visibleText + "']");

        click(trigger);
        click(option);
    }

    public void typeAndSelectFromDropdown(String inputXpath, String typedValue, String visibleOptionText) {
        // Delegate to shared logic from RecruitmentPage (DRY principle)
        new RecruitmentPage(driver, wait).typeAndSelectFromDropdown(inputXpath, typedValue, visibleOptionText);
    }

    public List<WebElement> getVacancyResults() {
        return driver.findElements(VACANCY_RESULTS);
    }

    // ---------- Helpers ----------

    private void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void type(By locator, String text) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).sendKeys(text);
    }
}
