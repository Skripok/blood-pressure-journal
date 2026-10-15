package com.skripok.bloodpressure.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private static final String URL = "http://localhost:8080/";

    private final By homeLink = By.id("navHome");
    private final By newMeasurementLink = By.id("navNewMeasurement");
    private final By historyLink = By.id("navHistory");
    private final By personsLink = By.id("navPersons");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(URL);
        waitForVisible(homeLink);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public void openNewMeasurement() {
        waitAndClick(newMeasurementLink);
    }

    public PersonsPage openHistory() {
        waitAndClick(historyLink);

        return new PersonsPage(driver);
    }

    public void openPersons() {
        waitAndClick(personsLink);
    }
}