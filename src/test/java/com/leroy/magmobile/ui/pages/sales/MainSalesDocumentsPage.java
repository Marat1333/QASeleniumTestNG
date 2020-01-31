package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import com.leroy.magmobile.ui.pages.common.OldSearchProductPage;
import com.leroy.magmobile.ui.pages.common.TopMenuPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import io.qameta.allure.Step;

// Раздел "Продажа" -> Страница "Документы продажи"
public class MainSalesDocumentsPage extends TopMenuPage {

    public MainSalesDocumentsPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "MainScreenTitle", metaName = "Строка поиска")
    Element searchArea;

    @AppFindBy(text = "Мои продажи")
    Element mySalesLbl;

    @AppFindBy(text = "Продажи моего отдела")
    Element myDepartmentSalesLbl;

    @AppFindBy(text = "Продажи моего магазина")
    Element myShopSalesLbl;

    @AppFindBy(text = "Все продажи")
    Element allSalesLbl;

    @AppFindBy(xpath = "//*[@text='СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ' or @text='ОФОРМИТЬ ПРОДАЖУ']",
            metaName = "Кнопка 'СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ' / 'ОФОРМИТЬ ПРОДАЖУ'")
    MagMobSubmitButton createSalesDocumentBtn;

    @Override
    public void waitForPageIsLoaded() {
        createSalesDocumentBtn.waitForVisibility();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажмите 'Создать документ продажи' (или 'Оформить продажу')")
    public <T> T clickCreateSalesDocumentButton() {
        createSalesDocumentBtn.click();
        if (context.isIs35Shop())
            return (T) new SaleTypeModalPage(context);
        else
            return (T) new OldSearchProductPage(context);
    }

    /* ---------------------- Verifications -------------------------- */

    @Step("Проверить, что страница 'Документы продажи' отображается корректно")
    public MainSalesDocumentsPage verifyRequiredElements() {
        softAssert.areElementsVisible(searchArea, createSalesDocumentBtn,
                mySalesLbl, myDepartmentSalesLbl, myShopSalesLbl, allSalesLbl);
        softAssert.verifyAll();
        return this;
    }
}
