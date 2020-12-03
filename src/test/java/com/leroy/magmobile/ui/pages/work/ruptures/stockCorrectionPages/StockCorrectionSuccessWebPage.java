package com.leroy.magmobile.ui.pages.work.ruptures.stockCorrectionPages;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class StockCorrectionSuccessWebPage extends BaseWebPage {

    @WebFindBy(containsText = "Карточка коррекции запаса отправлена")
    Element successHeader;

    @WebFindBy(containsText = "Закрыть")
    Element closeBtn;

    @Override
    protected void waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        successHeader.waitForVisibility();
    }

    @Step("Нажать \"закрыть\"")
    public void clickCloseBtn() {
        closeBtn.click();
    }
}