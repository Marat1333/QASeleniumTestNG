package com.leroy.magmobile.ui.models.sales;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.Data;

/**
 * Карточка товара внутри заказа
 * Экраны: Корзина, Смета и т.д.
 */
@Data
public class ProductOrderCardAppData {

    private String lmCode;
    private String barCode;
    private String title;
    private Double price;
    private String priceUnit;

    private Double selectedQuantity;
    private Double totalPrice;
    private Double totalPriceWithDiscount;
    private Integer availableTodayQuantity;
    private Double discountPercent;
    private boolean selectedMoreThanAvailable;

    public void assertEqualsNotNullExpectedFields(int index, ProductOrderCardAppData expectedProductCardData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        softAssert.isEquals(lmCode, expectedProductCardData.getLmCode(),
                "Товар " + (index + 1) + " - неверный ЛМ код");
        softAssert.isEquals(title, expectedProductCardData.getTitle(),
                "Товар " + (index + 1) + " - неверное название товара");
        softAssert.isEquals(price, expectedProductCardData.getPrice(),
                "Товар " + (index + 1) + " - неверная цена товара");
        softAssert.isEquals(priceUnit, expectedProductCardData.getPriceUnit(),
                "Товар " + (index + 1) + " - неверный price unit товара");
        softAssert.isEquals(selectedQuantity, expectedProductCardData.getSelectedQuantity(),
                "Товар " + (index + 1) + " - неверное выбранное кол-во товара");
        softAssert.isEquals(totalPrice, expectedProductCardData.getTotalPrice(),
                "Товар " + (index + 1) + " - неверная сумма товара");
        softAssert.isEquals(totalPriceWithDiscount, expectedProductCardData.getTotalPriceWithDiscount(),
                "Товар " + (index + 1) + " - неверная сумма (с учетом скидки) товара");
        if (expectedProductCardData.getAvailableTodayQuantity() != null) {
            softAssert.isEquals(availableTodayQuantity, expectedProductCardData.getAvailableTodayQuantity(),
                    "Товар " + (index + 1) + " - неверное доступное кол-во товара");
        }
        softAssert.isEquals(discountPercent, expectedProductCardData.getDiscountPercent(),
                "Товар " + (index + 1) + " - неверная скидка % товара");
        softAssert.verifyAll();
    }

    public void assertEqualsNotNullExpectedFields(ProductOrderCardAppData orderCardData) {
        assertEqualsNotNullExpectedFields(0, orderCardData);
    }

}
