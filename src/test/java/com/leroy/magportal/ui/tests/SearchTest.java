package com.leroy.magportal.ui.tests;

import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import org.testng.annotations.Test;

import java.time.LocalDate;

public class SearchTest extends WebBaseSteps {

    @Test(description = "C22782949 No results without filters")
    public void testNotFoundResults() throws Exception {
        LocalDate avsDate = LocalDate.of(2019, 5, 23);

        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.choseCheckboxFilter(SearchProductPage.Filters.AVS, false);
        searchProductPage.choseAvsDate(avsDate);
        searchProductPage.selectGammaFilter("Гамма P", "Гамма А");
        searchProductPage.choseNomenclature("011", "1115", "020", "040");
    }
}
