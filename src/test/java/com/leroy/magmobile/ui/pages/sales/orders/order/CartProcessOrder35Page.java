package com.leroy.magmobile.ui.pages.sales.orders.order;

import com.leroy.core.annotations.Form;
import com.leroy.magmobile.ui.models.sales.SalesDocumentData;
import com.leroy.magmobile.ui.pages.sales.orders.order.forms.ProductOrderForm;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;

/**
 * Оформление заказа. Шаг 1 - Список товаров (корзина)
 */
public class CartProcessOrder35Page extends HeaderProcessOrder35Page {

    @Form
    private ProductOrderForm productOrderForm;

    @Override
    public void waitForPageIsLoaded() {
        anAssert.isTrue(productOrderForm.waitUntilFormIsVisible(),
                "Экран 'Оформление заказа' с корзиной не отобразился");
    }

    // Grab info

    // Actions

    @Step("Нажмите кнопку для добавления товара в корзину")
    public SearchProductPage clickAddProductButton() {
        return productOrderForm.clickAddProductButton();
    }


    // Verifications

    @Step("Проверить, что данные корзины в заказе, как ожидались (expectedDocumentData)")
    public CartProcessOrder35Page shouldSalesDocumentDataIs(SalesDocumentData expectedDocumentData) {
        expectedDocumentData.setTitle(null);
        SalesDocumentData salesDocumentData = productOrderForm.getSalesDocumentData();
        salesDocumentData.assertEqualsNotNullExpectedFields(expectedDocumentData);
        return this;
    }

}
