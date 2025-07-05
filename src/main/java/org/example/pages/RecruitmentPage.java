package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RecruitmentPage extends BasePage {

    public RecruitmentPage(WebDriver driver) {
        super(driver);
    }

    public void navigateTo() {
        click(By.xpath("//span[text()='Recruitment']"));
    }

    public void addCandidate(String firstName, String lastName, String email) {
        click(By.xpath("//button[contains(@class, 'oxd-button') and contains(., 'Add')]"));
        type(By.name("firstName"), firstName);
        type(By.name("lastName"), lastName);
        type(By.xpath("(//input[@placeholder='Type here'])[1]"), email);
        click(By.xpath("//button[@type='submit']"));
    }

    public void searchCandidate(String name, String fullName) {
        typeAndSelectFromDropdown("//input[@placeholder='Type for hints...']", name, fullName);
        click(By.xpath("//button[text()=' Search ']"));
    }
}
