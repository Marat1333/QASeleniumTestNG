package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;

public class ScannerWithSearchBtnPage extends ScannerPage{
    @AppFindBy(containsText = "ВВЕСТИ ШТРИХ-КОД / ЛМ ВРУЧНУЮ")
    protected Button searchProductBtn;

    @Override
    protected void waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        searchProductBtn.waitForVisibility();
    }

    @Step("Искать товар вручную")
    public SearchProductPage navigateToSearchProductPage() {
        searchProductBtn.click();
        return new SearchProductPage();
    }

    @Override
    public void verifyRequiredElements() {
        super.verifyRequiredElements();
        softAssert.isElementVisible(searchProductBtn);
        softAssert.verifyAll();
    }
}
