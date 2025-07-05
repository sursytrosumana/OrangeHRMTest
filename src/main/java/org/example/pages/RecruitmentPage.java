package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.util.List;

public class RecruitmentPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public RecruitmentPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void goToVacancies() {
        goToRecruitment();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Vacancies']"))).click();
    }

    public void goToRecruitment() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Recruitment']"))).click();
    }

    public void addCandidate(String firstName, String lastName, String email) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'oxd-button') and contains(., 'Add')]"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstName"))).sendKeys(firstName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("lastName"))).sendKeys(lastName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@placeholder='Type here'])[1]"))).sendKeys(email);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']"))).click();
    }

    public void typeAndSelectFromDropdown(String inputXpath, String typedValue, String visibleOptionText) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(inputXpath)));
        input.clear();
        input.sendKeys(typedValue);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='listbox']")));

        List<String> xpathsToTry = List.of(
                "//div[@role='listbox']//div[normalize-space(text())='" + visibleOptionText + "']",
                "//div[@role='listbox']//div[contains(text(),'" + visibleOptionText + "')]",
                "//div[@role='listbox']//span[normalize-space(text())='" + visibleOptionText + "']",
                "//div[@role='listbox']//*[contains(text(),'" + visibleOptionText + "')]"
        );

        for (String xpath : xpathsToTry) {
            try {
                WebElement option = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", option);
                option.click();
                return;
            } catch (TimeoutException ignored) {}
        }

        throw new NoSuchElementException("Dropdown option not found: " + visibleOptionText);
    }

    public List<WebElement> getCandidateSearchResults() {
        return driver.findElements(By.xpath("//div[@class='oxd-table-card']"));
    }
}
