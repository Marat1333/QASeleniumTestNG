package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.product_card.SalesHistoryPage;
import io.qameta.allure.Step;

public class SalesHistoryUnitsModalPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "CloseModal")
    Button closeModalBtn;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'В количестве')]/following-sibling::*")
    Button quantityBtn;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'В деньгах')]/following-sibling::*")
    Button priceBtn;

    public enum Option {
        PRICE,
        QUANTITY
    }

    @Override
    public void waitForPageIsLoaded() {
        priceBtn.waitForVisibility();
        quantityBtn.waitForVisibility();
        closeModalBtn.waitForVisibility();
    }

    @Step("Выбрать единицу измерения")
    public SalesHistoryPage choseOption(Option option) {
        switch (option) {
            case PRICE:
                priceBtn.click();
                break;
            case QUANTITY:
                quantityBtn.click();
                break;
        }
        return new SalesHistoryPage();
    }
}
