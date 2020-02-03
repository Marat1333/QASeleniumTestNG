package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class ActionWithProduct35ModalPage extends CommonActionWithProductModalPage {

    public ActionWithProduct35ModalPage(TestContext context) {
        super(context);
    }

    @AppFindBy(text = "Оформить продажу")
    Element makeSaleBtn;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Оформить продажу']/following-sibling::android.view.ViewGroup/android.widget.TextView")
    Element makeSaleCountLbl;

    @AppFindBy(text = "Этого товара нет на складе")
    Element productNotExistOnStockBtn;

    @AppFindBy(text = "Пополнить торговый зал")
    Element replenishTradingRoomBtn;

    // ---------- ACTION STEPS --------------------------//
    @Step("Нажмите кнопку 'Оформить продажу'")
    public SaleTypeModalPage clickMakeSaleButton() {
        makeSaleBtn.click();
        return new SaleTypeModalPage(context);
    }

    // Verifications

    @Step("Проверить, что модальное окно 'Действия с товаром' отобразилось со всеми необходимыми товарами")
    public ActionWithProduct35ModalPage verifyRequiredElements(Boolean isProductExistOnStock, boolean isAvsProduct) {
        String ps = getPageSource();
        softAssert.isElementVisible(closeBtn, ps);
        softAssert.isElementVisible(headerLbl, ps);
        softAssert.isElementVisible(makeSaleBtn, ps);
        if (isProductExistOnStock != null) {
            if (isProductExistOnStock)
                softAssert.isElementVisible(replenishTradingRoomBtn);
            else
                softAssert.isElementVisible(productNotExistOnStockBtn);
        }
        if (isAvsProduct)
            softAssert.isElementNotVisible(notifyClientBtn, ps);
        else
            softAssert.isElementVisible(notifyClientBtn, ps);
        softAssert.verifyAll();
        return this;
    }

    @Override
    public ActionWithProduct35ModalPage verifyRequiredElements(boolean isAvsProduct) {
        return verifyRequiredElements(null, isAvsProduct);
    }
}
