package com.leroy.pages.app.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.pages.app.common.SearchProductPage;
import com.leroy.pages.app.common.TopMenuPage;
import io.qameta.allure.Step;

public class SalesPage extends TopMenuPage {

    public SalesPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "MainScreenTitle")
    EditBox searchBar;

    @Step("Переходим на страницу поиска")
    public SearchProductPage clickSearchBar(boolean hideKeyboard) {
        searchBar.waitForVisibility();
        searchBar.click();
        SearchProductPage page = new SearchProductPage(context);
        if (hideKeyboard)
            hideKeyboard();
        return page;
    }
}
