package com.leroy.magportal.ui.pages.orders.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.pages.picking.data.PickingData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

public class PickingWidget extends CardWebWidget<PickingData> {

    public PickingWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

   @WebFindBy(xpath = ".//div[contains(@class, 'shared-order-StyledLabel')]", metaName = "Номер отдела")
   Element department;

    // Actions

    @Override
    public PickingData collectDataFromPage() {
        /*ProductOrderCardWebData productOrderCardWebData = new ProductOrderCardWebData();
        productOrderCardWebData.setBarCode(ParserUtil.strWithOnlyDigits(barCode.getText()));
        productOrderCardWebData.setLmCode(lmCode.getText());
        //productOrderCardWebData.setDepartment(ParserUtil.strToInt(department.getText()));
        //productOrderCardWebData.setDimension(dimension.getText());
        productOrderCardWebData.setTitle(title.getText());
        productOrderCardWebData.setPrice(ParserUtil.strToDouble(price.getText()));
        productOrderCardWebData.setSelectedQuantity(ParserUtil.strToDouble(orderedQuantityFld.getText()));
        productOrderCardWebData.setWeight(ParserUtil.strToDouble(weight.getText(), ".") * productOrderCardWebData.getSelectedQuantity());
        productOrderCardWebData.setTotalPrice(productOrderCardWebData.getPrice() * productOrderCardWebData.getSelectedQuantity());*/
        return null;
    }

    // Examples

    @WebFindBy(xpath = ".//div[span[contains(@class, 'LmCode__accent')]]", metaName = "ЛМ код")
    Element lmCode;

    @WebFindBy(xpath = ".//div[contains(@class, 'Dropdown__title')]//button", metaName = "Бар код")
    Element barCode;

    @WebFindBy(xpath = ".//p[contains(@class, 'ProductCard__body-title')]", metaName = "Название товара")
    Element title;

    @WebFindBy(xpath = ".//span[contains(@class, 'Price-container')]", metaName = "Цена")
    Element price;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCardFooter__smallSide')]//p[2]", metaName = "Вес")
    Element weight;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCardFooter__bigSide')]//p[2]", metaName = "Габариты")
    Element dimension;

    @WebFindBy(xpath = ".//div[contains(@class, 'Dropdown--center lmui-Dropdown')]//span", metaName = "Кол-во 'Доступно'")
    Element availableQuantity;

    public String getAvailableQuantity() {
        return availableQuantity.getText();
    }

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__quantities')]//input",
            metaName = "Поле 'Заказано'")
    EditBox orderedQuantityFld;

    public String getOrderedQuantity() {
        return orderedQuantityFld.getText();
    }

}

