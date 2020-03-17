package com.leroy.magportal.ui.tests;

import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import org.testng.annotations.Test;

import java.time.LocalDate;

public class SearchTest extends WebBaseSteps {

    @Test(description = "C22782949 No results msg")
    public void testNotFoundResults() throws Exception {
        final String SEARCH_PHRASE= "asdf123";

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        searchProductPage.searchByPhrase(SEARCH_PHRASE);
        searchProductPage.shouldNotFoundMsgIsDisplayed(false, SEARCH_PHRASE);

        //Step 2
        searchProductPage.choseCheckboxFilter(SearchProductPage.Filters.HAS_AVAILABLE_STOCK, true);
        searchProductPage.shouldNotFoundMsgIsDisplayed(true, SEARCH_PHRASE);
    }
}
