package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaleTypeModalPage extends CommonMagMobilePage {

    public SaleTypeModalPage(TestContext context) {
        super(context);
    }

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
    }

    @AppFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"Button\"])[1]/android.view.ViewGroup",
            metaName = "Кнопка для закрытия модального окна")
    Element closeBtn;

    @AppFindBy(text = "Тип продажи")
    Element headerLbl;

    @AppFindBy(text = "Корзина")
    Element basketBtn;

    @AppFindBy(text = "Из торгового зала")
    Element fromTradingRoomBtn;

    @AppFindBy(text = "Этого товара нет на складе")
    Element productIsNotPresentOnStockLbl;
    @AppFindBy(text = "Со склада клиенту")
    Element fromStockToCustomerLbl;

    @AppFindBy(text = "Смета")
    Element estimateBtn;

    // ACTIONS

    @Step("Нажмите пункт меню 'Корзина'")
    public AddProduct35Page clickBasketMenuItem() {
        basketBtn.click();
        return new AddProduct35Page(context);
    }

    // VERIFICATIONS

    @Step("Проверить, что модальное окно 'Тип продажи' отображается корректно")
    public SaleTypeModalPage verifyRequiredElements(boolean isPresentOnStock) {
        List<Element> expectedElements = new ArrayList<>(Arrays.asList(
                headerLbl, closeBtn, basketBtn, fromTradingRoomBtn, estimateBtn));
        if (isPresentOnStock)
            expectedElements.add(fromStockToCustomerLbl);
        else
            expectedElements.add(productIsNotPresentOnStockLbl);
        softAssert.areElementsVisible(expectedElements.toArray(new Element[0]));
        softAssert.verifyAll();
        return this;
    }

}
