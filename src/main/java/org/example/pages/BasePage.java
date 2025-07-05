package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    protected void selectDropdown(String label, String valueToSelect) {
        By dropdown = By.xpath("//label[text()='" + label + "']/following::div[contains(@class,'oxd-select-text')]");
        click(dropdown);
        By option = By.xpath("//div[@role='listbox']//span[text()='" + valueToSelect + "']");
        click(option);
    }

    protected void typeAndSelectFromDropdown(String inputXpath, String typedValue, String visibleOptionText) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(inputXpath)));
        input.clear();
        input.sendKeys(typedValue);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='listbox']")));
        By option = By.xpath("//div[@role='listbox']//*[contains(text(),'" + visibleOptionText + "')]");
        click(option);
    }
}
