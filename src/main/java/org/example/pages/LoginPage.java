package org.example.pages;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

public class LoginPage {
    //declaration
    private WebDriver driver;
    private WebDriverWait wait;

    //initializing through constructor
    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void login(String username, String password, ExtentTest test) {
        try {
            test.info("Waiting for username field");
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));

            test.info("Waiting for password field");
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));

            test.info("Waiting for login button");
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));

            test.info("Entering username: " + username);
            usernameField.clear();
            usernameField.sendKeys(username);

            test.info("Entering password");
            passwordField.clear();
            passwordField.sendKeys(password);

            test.info("Clicking login button");
            loginButton.click();

            test.pass("Login action performed successfully");
        } catch (Exception e) {
            test.fail("Login failed: " + e.getMessage());
            throw e;
        }
    }
}
