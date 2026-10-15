package com.skripok.bloodpressure.selenium;

import com.skripok.bloodpressure.selenium.pages.HomePage;
import com.skripok.bloodpressure.selenium.pages.PersonsPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomePageTest extends BaseTest {

    @Test
    void shouldOpenHomePage() throws InterruptedException {

        final HomePage homePage = new HomePage(driver);

        homePage.open();

        assertTrue(homePage.getTitle().contains("Журнал"));

    }


    @Test
    void shouldNavigateToHistory() {
        final HomePage homePage = new HomePage(driver);

        homePage.open();

        final PersonsPage personsPage = homePage.openHistory();

        assertTrue(
                personsPage.isHistoryModeOpened(),
                "Navigation should open patient history selection"
        );

    }
}