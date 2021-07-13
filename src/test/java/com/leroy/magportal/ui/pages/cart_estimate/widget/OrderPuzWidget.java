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

    Element totalPriceValue;
    Element footerQuantity;
    Element footerTotalWeight;

    private void initFooterElements() {
        String footerXpath = E("//div[contains(@class, 'View__footer')]").isPresent() ?
                FOOTER_INFO_XPATH : getXpath() + FOOTER_INFO_XPATH;
        footerQuantity = E("//span[@data-testid='footerQuantity']",
                "Информация о кол-ве товаров");
        footerTotalWeight = E("//span[@data-testid='footerTotalWeight']",
                "Информация о весе товаров");
        totalPriceValue = E(footerXpath + "//span[@data-testid='lmui-Price']",
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

    public List<ProductOrderCardWebData> getProductDataList() throws Exception {
        return products.getDataList();
    }

    @Override
    public OrderWebData collectDataFromPage() throws Exception {
        initFooterElements();
        OrderWebData orderWebData = new OrderWebData();
        orderWebData.setProductCardDataList(products.getDataList());

        orderWebData.setTotalPrice(ParserUtil.strToDouble(totalPriceValue.getText()));
        orderWebData.setTotalWeight(ParserUtil.strToWeight(footerTotalWeight.getText()));
        orderWebData.setProductCount(ParserUtil.strToInt(footerQuantity.getText()));

        return orderWebData;
    }
}
