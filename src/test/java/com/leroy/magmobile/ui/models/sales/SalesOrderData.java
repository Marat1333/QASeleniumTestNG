package com.leroy.magmobile.ui.models.sales;

import com.leroy.magmobile.ui.models.CardWidgetData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public class SalesOrderData extends CardWidgetData {

    private List<SalesOrderCardData> orderCardDataList;
    private Integer productCount;
    private Double totalWeight; // кг
    private Double totalPrice; // Рубли

}
