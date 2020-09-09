package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.work.transfer.TransferOrderStep1Page;
import io.qameta.allure.Step;

public class ActionWithProduct35ModalPage extends CommonActionWithProductModalPage {

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
        return new SaleTypeModalPage();
    }

    @Step("Нажмите кнопку 'Пополнить торговый зал'")
    public AddProduct35Page<TransferOrderStep1Page> clickFillShoppingRoomButton() {
        replenishTradingRoomBtn.click();
        return new AddProduct35Page<>(TransferOrderStep1Page.class);
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
