package com.homedepot.tests;

import com.homedepot.core.BaseTest;
import com.homedepot.core.ConfigReader;
import com.homedepot.core.TestLog;
import com.homedepot.pages.HomePage;
import com.homedepot.pages.SearchResultsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GuestCheckoutE2ETest extends BaseTest {

    /*
     * Search for hammer
     * Verify results are present
     * Close browser (handled in tearDown)
     */

    @Test(timeOut = TEST_TIMEOUT_MS, description = "Search hammer and verify results are present")
    public void searchHammerAndVerifyResults() {
        String searchTerm = ConfigReader.getSearchTerm();

        TestLog.info("Step 1: Search for product '" + searchTerm + "'");
        SearchResultsPage searchResults = new HomePage(driver, waitUtils).search(searchTerm);

        TestLog.info("Step 2: Verify search results are present");
        Assert.assertTrue(searchResults.hasResults(), "Search results should be present for '" + searchTerm + "'");

        TestLog.info("Step 3: Close browser and end test");
    }
}
