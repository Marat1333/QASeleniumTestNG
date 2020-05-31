package com.leroy.magmobile.ui.models.sales;

import lombok.Data;

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

}
