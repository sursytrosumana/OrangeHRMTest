package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.util.List;

public class VacanciesPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public VacanciesPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void addVacancy(String vacancyName, String jobTitle, String hiringManager) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class,'orangehrm-header-container')]//button[contains(., 'Add')]"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Vacancy Name']/following::input[1]"))).sendKeys(vacancyName);
        selectDropdown("Job Title", jobTitle);
        typeAndSelectFromDropdown("//label[text()='Hiring Manager']/following::input[1]", hiringManager, hiringManager);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit' and normalize-space()='Save']"))).click();
    }

    public void selectDropdown(String label, String valueToSelect) {
        By dropdownTrigger = By.xpath("//label[text()='" + label + "']/following::div[contains(@class,'oxd-select-text')]");
        wait.until(ExpectedConditions.elementToBeClickable(dropdownTrigger)).click();

        By optionLocator = By.xpath("//div[@role='listbox']//span[text()='" + valueToSelect + "']");
        wait.until(ExpectedConditions.elementToBeClickable(optionLocator)).click();
    }

    public void typeAndSelectFromDropdown(String inputXpath, String typedValue, String visibleOptionText) {
        new RecruitmentPage(driver, wait).typeAndSelectFromDropdown(inputXpath, typedValue, visibleOptionText);
    }

    public List<WebElement> getVacancyResults() {
        return driver.findElements(By.xpath("//div[@class='oxd-table-card']"));
    }
}
