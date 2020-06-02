package com.leroy.magmobile.ui.models.sales;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Инфа о продуктах внутри заказа и суммарная информация о них (Итого стоимость, вес и т.д.)
 */
@Data
public class OrderAppData {

    private Integer orderIndex; // Заказ {orderIndex} из {orderMaxCount}
    private Integer orderMaxCount;
    private Integer productCount;
    private Integer productCountTotal; // Товаров: {productCount} из {productCountTotal}
    private String date;
    private List<ProductOrderCardAppData> productCardDataList;
    private Double totalWeight; // кг
    private Double totalPrice; // Рубли

    public void removeProduct(int index, boolean recalculateOrder) {
        if (recalculateOrder) {
            ProductOrderCardAppData removeProduct = productCardDataList.get(index);
            totalPrice -= removeProduct.getTotalPrice();
            productCount--;
        }
        productCardDataList.remove(index);
    }

    public void addFirstProduct(ProductOrderCardAppData product, boolean recalculateOrder) {
        List<ProductOrderCardAppData> result = new ArrayList<>();
        result.add(product);
        result.addAll(productCardDataList);
        productCardDataList = result;
        if (recalculateOrder) {
            productCount++;
            totalPrice += product.getTotalPrice();
        }
    }

    public void assertEqualsNotNullExpectedFields(int index, OrderAppData expectedOrderCardData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedOrderCardData.getOrderIndex() != null) {
            softAssert.isEquals(orderIndex, expectedOrderCardData.getOrderIndex(),
                    "Заказ " + (index + 1) + " - неверный номер/индекс заказа");
        }
        if (expectedOrderCardData.getOrderMaxCount() != null) {
            softAssert.isEquals(orderMaxCount, expectedOrderCardData.getOrderMaxCount(),
                    "Заказ " + (index + 1) + " - неверное общее кол-во заказов");
        }
        if (expectedOrderCardData.getProductCountTotal() != null) {
            softAssert.isEquals(productCountTotal, expectedOrderCardData.getProductCountTotal(),
                    "Заказ " + (index + 1) + " - неверное общее кол-во товаров");
        }
        softAssert.isEquals(productCount, expectedOrderCardData.getProductCount(),
                "Заказ " + (index + 1) + " - неверное кол-во товаров в заказе");
        if (expectedOrderCardData.getDate() != null) {
            softAssert.isEquals(date, expectedOrderCardData.getDate(),
                    "Заказ " + (index + 1) + " - неверная дата исполнения заказа");
        }

        if (expectedOrderCardData.getTotalWeight() != null) {
            softAssert.isEquals(BigDecimal.valueOf(totalWeight).setScale(1, RoundingMode.HALF_UP),
                    BigDecimal.valueOf(expectedOrderCardData.getTotalWeight()).setScale(1, RoundingMode.HALF_UP),
                    "Заказ " + (index + 1) + " - неверный вес заказа");
        }
        softAssert.isEquals(totalPrice, expectedOrderCardData.getTotalPrice(),
                "Заказ " + (index + 1) + " - неверная стоимость заказа");
        softAssert.isEquals(productCardDataList.size(), expectedOrderCardData.getProductCardDataList().size(),
                "Разное кол-во товаров в заказе");
        softAssert.verifyAll();

        for (int i = 0; i < expectedOrderCardData.getProductCardDataList().size(); i++) {
            productCardDataList.get(i).assertEqualsNotNullExpectedFields(i, expectedOrderCardData.getProductCardDataList().get(i));
        }

        softAssert.verifyAll();
    }

    public void assertEqualsNotNullExpectedFields(OrderAppData expectedOrderCardData) {
        assertEqualsNotNullExpectedFields(0, expectedOrderCardData);
    }

}
