package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.basket.Basket35Page;
import com.leroy.magmobile.ui.pages.sales.estimate.EstimatePage;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 1) Из карточки товара -> Действия с товаром -> Оформить продажу
// 2) Продажа -> Документы продажи -> Оформить продажу
public class SaleTypeModalPage extends CommonMagMobilePage {

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
    }

    @AppFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"Button\"])[1]/android.view.ViewGroup",
            metaName = "Кнопка для закрытия модального окна (вернуться назад)")
    Element backBtn;

    @AppFindBy(accessibilityId = "CloseModal",
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
    public <T> T clickBasketMenuItem() {
        basketBtn.click();
        headerLbl.waitForInvisibility();
        // Плохо так хардкодить. Не повторяйте:
        if (E("//*[contains(@content-desc, 'Screen')]//android.widget.TextView")
                .getText().equals(Basket35Page.SCREEN_TITLE))
            return (T) new Basket35Page();
        else
            return (T) new AddProduct35Page();

    }

    @Step("Нажмите пункт меню 'Смета'")
    public EstimatePage clickEstimateMenuItem() {
        estimateBtn.click();
        return new EstimatePage();
    }

    // VERIFICATIONS
    private void verifyRequiredElements(boolean isFromProductCard, boolean isPresentOnStock) {
        List<Element> expectedElements = new ArrayList<>(Arrays.asList(
                headerLbl, basketBtn, fromTradingRoomBtn, estimateBtn));
        expectedElements.add(isPresentOnStock ? fromStockToCustomerLbl : productIsNotPresentOnStockLbl);
        expectedElements.add(isFromProductCard ? backBtn : closeBtn);
        softAssert.areElementsVisible(expectedElements.toArray(new Element[0]));
        softAssert.verifyAll();
    }

    @Step("Проверить, что модальное окно 'Тип продажи' отображается корректно")
    public SaleTypeModalPage verifyRequiredElementsWhenFromProductCard(boolean isPresentOnStock) {
        verifyRequiredElements(true, isPresentOnStock);
        return this;
    }

    @Step("Проверить, что модальное окно 'Тип продажи' отображается корректно")
    public SaleTypeModalPage verifyRequiredElementsWhenFromSalesDocuments() {
        verifyRequiredElements(false, true);
        return this;
    }

}
