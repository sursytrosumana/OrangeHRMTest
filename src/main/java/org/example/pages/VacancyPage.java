package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class VacancyPage extends BasePage {

    public VacancyPage(WebDriver driver) {
        super(driver);
    }

    public void navigateTo() {
        click(By.xpath("//span[text()='Recruitment']"));
        click(By.xpath("//a[text()='Vacancies']"));
    }

    public void addVacancy(String vacancyName, String jobTitle, String hiringManager) {
        click(By.xpath("//div[contains(@class,'orangehrm-header-container')]//button[contains(., 'Add')]"));
        type(By.xpath("//label[text()='Vacancy Name']/following::input[1]"), vacancyName);
        selectDropdown("Job Title", jobTitle);
        typeAndSelectFromDropdown("//label[text()='Hiring Manager']/following::input[1]", hiringManager, hiringManager);
        click(By.xpath("//button[text()='Save']"));
    }

    public void searchVacancy(String jobTitle) {
        selectDropdown("Job Title", jobTitle);
        click(By.xpath("//button[@type='submit' and contains(.,'Search')]"));
    }
}
