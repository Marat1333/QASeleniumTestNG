package com.leroy.magmobile.ui.models.sales;

import com.leroy.magmobile.ui.models.CardWidgetData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public class SalesOrderData extends CardWidgetData {

    private List<SalesOrderCardData> orderCardDataList;
    private Integer productCount;
    private Double totalWeight; // кг
    private Double totalPrice; // Рубли

    public void removeProduct(int index, boolean recalculateOrder) {
        if (recalculateOrder) {
            SalesOrderCardData removeProduct = orderCardDataList.get(index);
            totalPrice -= removeProduct.getTotalPrice();
            productCount--;
        }
        orderCardDataList.remove(index);
    }

    public void addFirstProduct(SalesOrderCardData product, boolean recalculateOrder) {
        List<SalesOrderCardData> result = new ArrayList<>();
        result.add(product);
        result.addAll(orderCardDataList);
        orderCardDataList = result;
        if (recalculateOrder) {
            productCount++;
            totalPrice += product.getTotalPrice();
        }
    }

}
