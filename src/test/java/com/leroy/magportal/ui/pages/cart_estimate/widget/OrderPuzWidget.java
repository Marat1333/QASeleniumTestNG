package com.leroy.magportal.ui.pages.cart_estimate.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.List;

/**
 * Карточки с заказами в смете и корзине.
 * (В смете может быть только 1 заказ, в корзине несколько)
 * Каждый заказ может включать в себя несколько карточек с продуктами @products
 */
public class OrderPuzWidget extends CardWebWidget<OrderWebData> {

    public OrderPuzWidget(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct') and not(contains(@class, 'SalesDocProduct_'))]",
            clazz = ProductOrderCardPuzWidget.class)
    CardWebWidgetList<ProductOrderCardPuzWidget, ProductOrderCardWebData> products;

    private final String FOOTER_INFO_XPATH = "//div[contains(@class, 'SalesDoc-ViewFooter__info')]";

    Element countAndWeightProductLbl;
    Element totalPriceValue;

    private void initFooterElements() {
        String footerXpath = E("//div[contains(@class, 'View__footer')]").isPresent() ?
                FOOTER_INFO_XPATH : getXpath() + FOOTER_INFO_XPATH;
        countAndWeightProductLbl = E(footerXpath + "/span[1]",
                "Информация о кол-ве товаров и их общем весе");
        totalPriceValue = E(footerXpath + "/div/span[1]",
                "Значение суммы итого");
    }

    public ProductOrderCardPuzWidget getProductWidget(int index) throws Exception {
        int productCount = products.getCount();
        Assert.assertTrue(productCount > index, String.format(
                "Кол-во товаров %s, вы пытались обратиться к %s-ому", productCount, index + 1));
        return products.get(index);
    }

    public CardWebWidgetList<ProductOrderCardPuzWidget, ProductOrderCardWebData> getProductWidgets() throws Exception {
        return products;
    }

    public List<ProductOrderCardWebData> getProductDataList() {
        return products.getDataList();
    }

    @Override
    public OrderWebData collectDataFromPage() {
        initFooterElements();
        OrderWebData orderWebData = new OrderWebData();
        orderWebData.setProductCardDataList(products.getDataList());

        String[] productCountAndWeight = countAndWeightProductLbl.getText().split("•");
        Assert.assertEquals(productCountAndWeight.length, 2,
                "Что-то изменилось в метке содержащей информацию о кол-ве и весе товара");

        orderWebData.setTotalPrice(ParserUtil.strToDouble(totalPriceValue.getText()));
        orderWebData.setTotalWeight(ParserUtil.strToDouble(productCountAndWeight[1]));
        orderWebData.setProductCount(ParserUtil.strToInt(productCountAndWeight[0]));

        return orderWebData;
    }
}
