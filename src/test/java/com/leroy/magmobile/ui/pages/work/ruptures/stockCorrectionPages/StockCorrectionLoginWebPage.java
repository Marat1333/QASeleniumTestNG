package com.leroy.magmobile.ui.pages.work.ruptures.stockCorrectionPages;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class StockCorrectionLoginWebPage extends BaseWebPage {

    @WebFindBy(text = "ВОЙТИ")
    Element logIdBtn;

    @Override
    protected void waitForPageIsLoaded() {
        logIdBtn.waitForVisibility();
    }

    @Step("Нажмите кнопку 'ВОЙТИ'")
    public StockCorrectionAddProductWebPage clickLogIdBtn() {
        logIdBtn.click();
        return new StockCorrectionAddProductWebPage();
    }
}
