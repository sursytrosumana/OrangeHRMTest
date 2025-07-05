package org.example.pages;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.util.List;

public class VacanciesPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    By addVacancy = By.xpath("//div[contains(@class,'orangehrm-header-container')]//button[contains(., 'Add')]");
    By nameVacancy = By.xpath("//label[text()='Vacancy Name']/following::input[1]");
    By saveVacancy = By.xpath("//button[@type='submit' and normalize-space()='Save']");
    By vacancyResults = By.xpath("//div[@class='oxd-table-card']");

    public VacanciesPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void addVacancy(String vacancyName, String jobTitle, String hiringManager, ExtentTest test) {
        try {
            test.info("Clicking 'Add Vacancy' button");
            click(addVacancy);

            test.info("Typing vacancy name: " + vacancyName);
            type(nameVacancy, vacancyName);

            test.info("Selecting job title: " + jobTitle);
            selectDropdown("Job Title", jobTitle, test);

            test.info("Selecting hiring manager: " + hiringManager);
            typeAndSelectFromDropdown(
                    "//label[text()='Hiring Manager']/following::input[1]",
                    hiringManager,
                    hiringManager,
                    test
            );

            test.info("Clicking 'Save' button");
            click(saveVacancy);

            test.pass("Vacancy added: " + vacancyName);
        } catch (Exception e) {
            test.fail("Failed to add vacancy: " + e.getMessage());
            throw e;
        }
    }


    public void selectDropdown(String label, String visibleText, ExtentTest test) {
        try {
            test.info("Selecting from dropdown labeled: " + label + " → " + visibleText);
            By trigger = By.xpath("//label[text()='" + label + "']/following::div[contains(@class,'oxd-select-text')]");
            By option = By.xpath("//div[@role='listbox']//span[text()='" + visibleText + "']");

            click(trigger);
            click(option);
            test.pass("Selected option: " + visibleText);
        } catch (Exception e) {
            test.fail("Failed to select dropdown value: " + e.getMessage());
            throw e;
        }
    }

    public void typeAndSelectFromDropdown(String inputXpath, String typedValue, String visibleOptionText, ExtentTest test) {
        new RecruitmentPage(driver, wait).typeAndSelectFromDropdown(inputXpath, typedValue, visibleOptionText, test);
    }

    public List<WebElement> getVacancyResults() {
        return driver.findElements(vacancyResults);
    }


    private void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void type(By locator, String text) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).sendKeys(text);
    }
}
