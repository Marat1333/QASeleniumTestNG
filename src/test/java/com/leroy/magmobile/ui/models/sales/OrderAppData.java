package com.leroy.magmobile.ui.models.sales;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.utils.ParserUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Инфа о продуктах внутри заказа и суммарная информация о них (Итого стоимость, вес и т.д.)
 */
@Data
public class OrderAppData {

    private Integer orderIndex; // Заказ {orderIndex} из {orderMaxCount}
    private Integer orderMaxCount;
    private Integer productCount;
    private Integer productCountTotal; // Товаров: {productCount} из {productCountTotal}
    private LocalDate date;
    private List<ProductOrderCardAppData> productCardDataList;
    private Double totalWeight; // кг
    private Double totalPrice; // Рубли

    public void removeDiscountProduct(int productIndex) {
        setDiscountPercentToProduct(productIndex, 0);
    }

    public void setDiscountPercentToProduct(int productIndex, double discountPercent) {
        ProductOrderCardAppData product = productCardDataList.get(productIndex);
        Double discountAmountBefore = product.getTotalPrice() - (product.getTotalPriceWithDiscount() != null ?
                product.getTotalPriceWithDiscount() : product.getTotalPrice());
        product.setDiscountPercent(discountPercent, true);
        this.totalPrice = ParserUtil.minus(
                totalPrice + discountAmountBefore, (product.getTotalPrice() * discountPercent / 100), 2);
    }

    public void removeProduct(int index) {
        ProductOrderCardAppData removeProduct = productCardDataList.get(index);
        totalPrice -= removeProduct.getTotalPrice();
        productCount--;
        productCardDataList.remove(index);
    }

    public void addFirstProduct(ProductOrderCardAppData product) {
        List<ProductOrderCardAppData> result = new ArrayList<>();
        result.add(product);
        result.addAll(productCardDataList);
        productCardDataList = result;
        productCount++;
        totalPrice += product.getTotalPrice();
    }

    public void changeProductQuantity(int index, double quantity) {
        ProductOrderCardAppData product = productCardDataList.get(index);
        changeProductQuantity(product, quantity);
    }

    public void changeProductQuantity(ProductOrderCardAppData product, double quantity) {
        double price = product.getPrice();
        double quantityDiff = quantity - product.getSelectedQuantity();
        product.setSelectedQuantity(quantity);
        product.setTotalPrice(price * quantity);
        totalPrice += ParserUtil.multiply(price, quantityDiff, 2);
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
            if (totalWeight > 1000 || expectedOrderCardData.getTotalWeight() > 1000) {
                softAssert.isTrue(Math.abs(totalWeight - expectedOrderCardData.getTotalWeight()) < 10,
                        "Заказ " + (index + 1) +
                                " - неверный вес заказа. Actual: " + totalWeight +
                                "; Expected: " + expectedOrderCardData.getTotalWeight());
            } else {
                softAssert.isEquals(BigDecimal.valueOf(totalWeight).setScale(1, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(expectedOrderCardData.getTotalWeight()).setScale(1, RoundingMode.HALF_UP),
                        "Заказ " + (index + 1) + " - неверный вес заказа");
            }
        }
        softAssert.isEquals(totalPrice, expectedOrderCardData.getTotalPrice(),
                "Заказ " + (index + 1) + " - неверная стоимость заказа");
        softAssert.isEquals(productCardDataList.size(), expectedOrderCardData.getProductCardDataList().size(),
                "Разное кол-во товаров в заказе");
        softAssert.verifyAll();

        for (int i = 0; i < expectedOrderCardData.getProductCardDataList().size(); i++) {
            int iCount = i;
            Optional<ProductOrderCardAppData> expProduct = expectedOrderCardData.getProductCardDataList().stream().filter(
                    p -> p.getLmCode().equals(productCardDataList.get(iCount).getLmCode()) &&
                            p.getSelectedQuantity().equals(productCardDataList.get(iCount).getSelectedQuantity()))
                    .findFirst();
            if (!expProduct.isPresent()) {
                expProduct = expectedOrderCardData.getProductCardDataList().stream().filter(
                        p -> p.getLmCode().equals(productCardDataList.get(iCount).getLmCode())).findFirst();
            }
            softAssert.isTrue(expProduct.isPresent(),
                    "Заказ " + (index + 1) + " - обнаружен лишний товар с ЛМ " +
                            productCardDataList.get(iCount).getLmCode());
            if (expProduct.isPresent())
                productCardDataList.get(iCount).assertEqualsNotNullExpectedFields(i, expProduct.get());
        }

        softAssert.verifyAll();
    }

    public void assertEqualsNotNullExpectedFields(OrderAppData expectedOrderCardData) {
        assertEqualsNotNullExpectedFields(0, expectedOrderCardData);
    }

}
