package com.skripok.bloodpressure.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PersonsPage extends BasePage {

    public PersonsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isHistoryModeOpened() {
        return wait.until(
                ExpectedConditions.urlContains("/persons/history")
        );
    }

}