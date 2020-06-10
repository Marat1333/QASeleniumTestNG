package com.leroy.magmobile.ui.pages.sales.orders.order.forms;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.sales.OrderAppData;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.models.sales.SalesDocumentData;
import com.leroy.magmobile.ui.pages.sales.widget.ProductOrderCardAppWidget;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Форма с товарами и суммарной информацией о них
 */
public class ProductOrderForm extends BaseAppPage {

    @AppFindBy(xpath = "//android.widget.TextView[@text='Товары доступны']/following::android.widget.TextView[@content-desc='Badge-Text']",
            metaName = "Дата осуществления заказа")
    Element dateOrder;

    // Карточки товаров
    AndroidScrollView<ProductOrderCardAppData> productCardsScrollView = new AndroidScrollView<>(
            driver, AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]",
            ProductOrderCardAppWidget.class
    );

    // Bottom Area
    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/preceding-sibling::android.widget.TextView",
            metaName = "Текст с количеством и весом товара")
    Element countAndWeightProductLbl;

    @AppFindBy(text = "Итого: ")
    Element totalPriceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/following-sibling::android.widget.TextView")
    Element totalPriceVal;

    public boolean waitUntilFormIsVisible() {
        return countAndWeightProductLbl.waitForVisibility();
    }

    // Grab info

    public int getProductCount(String ps) {
        String[] actualCountProductAndWeight = countAndWeightProductLbl.getText(ps).split("•");
        return ParserUtil.strToInt(actualCountProductAndWeight[0]);
    }

    public Double getTotalWeight(String ps) {
        String[] actualCountProductAndWeight = countAndWeightProductLbl.getText(ps).split("•");
        double weight = ParserUtil.strToDouble(actualCountProductAndWeight[1]);
        return actualCountProductAndWeight[1].endsWith("кг") ? weight : weight * 1000;
    }

    public Double getTotalPrice() {
        return getTotalPrice(null);
    }

    private Double getTotalPrice(String ps) {
        return ParserUtil.strToDouble(totalPriceVal.getText(ps));
    }

    public SalesDocumentData getSalesDocumentData() {
        List<OrderAppData> actualOrderDataList = new ArrayList<>();
        SalesDocumentData salesDocumentData = new SalesDocumentData();
        List<ProductOrderCardAppData> products = productCardsScrollView.getFullDataList();
        String ps = getPageSource();
        OrderAppData orderAppData = new OrderAppData();
        orderAppData.setTotalWeight(getTotalWeight(ps));
        orderAppData.setTotalPrice(getTotalPrice(ps));
        orderAppData.setDate(DateTimeUtil.strToLocalDate(dateOrder.getText(ps), "dd MMM"));
        orderAppData.setProductCount(getProductCount(ps));
        orderAppData.setProductCardDataList(products);
        actualOrderDataList.add(orderAppData);
        salesDocumentData.setOrderAppDataList(actualOrderDataList);
        return salesDocumentData;
    }

}
