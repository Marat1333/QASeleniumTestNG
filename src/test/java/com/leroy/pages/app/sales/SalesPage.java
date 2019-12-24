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
    EditBox searchString;

    @Step("Переходим на страницу поиска")
    public SearchProductPage selectSearchString(){
        searchString.click();
        return new SearchProductPage(context);
    }
}
