package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.AddProductPage;
import io.qameta.allure.Step;

public class AddIntoSalesDocumentModalPage extends CommonMagMobilePage {

    public AddIntoSalesDocumentModalPage(TestContext context) {
        super(context);
    }

    @Override
    public void waitForPageIsLoaded() {
        createSalesDocumentBtn.waitForVisibility();
    }

    @AppFindBy(text = "СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ")
    private MagMobSubmitButton createSalesDocumentBtn;

    // ---- ACTIONS ---------//

    @Step("Нажать кнопку 'СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ'")
    public AddProductPage clickCreateSalesDocumentBtn() {
        createSalesDocumentBtn.click();
        return new AddProductPage(context);
    }

}
