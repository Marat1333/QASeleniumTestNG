package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.common.TopMenuPage;
import io.qameta.allure.Step;

// Продажа -> Товары и услуги
public class MainProductAndServicesPage extends TopMenuPage {

    public MainProductAndServicesPage(Context context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "MainScreenTitle")
    EditBox searchBar;

    @Override
    public void waitForPageIsLoaded() {
        searchBar.waitForVisibility();
    }

    // ACTIONS

    @Step("Переходим на страницу поиска")
    public SearchProductPage clickSearchBar(boolean hideKeyboard) {
        searchBar.click();
        SearchProductPage page = new SearchProductPage(context);
        if (hideKeyboard)
            hideKeyboard();
        return page;
    }
}
