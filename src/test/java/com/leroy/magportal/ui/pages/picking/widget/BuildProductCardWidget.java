package com.leroy.magportal.ui.pages.picking.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.picking.PickingProductCardData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class BuildProductCardWidget extends CardWebWidget<PickingProductCardData> {

    public BuildProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//div[contains(@class, 'shared-order-StyledLabel')]", metaName = "Номер отдела")
    Element department;

    @WebFindBy(xpath = ".//div[span[contains(@class, 'LmCode__accent')]]", metaName = "ЛМ код")
    Element lmCode;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__barcodes')]", metaName = "Бар код")
    Element barCode;

    @WebFindBy(xpath = ".//p[contains(@class, 'ProductCard__body-title')]", metaName = "Название товара")
    Element title;

    @WebFindBy(xpath = ".//span[contains(@class, 'Price-container')]", metaName = "Цена")
    Element price;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCardFooter__smallSide')]//p[2]", metaName = "Вес")
    Element weight;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCardFooter__bigSide')]//p[2]", metaName = "Габариты")
    Element dimension;

    @Override
    public PickingProductCardData collectDataFromPage() {
        PickingProductCardData pickingProductCardData = new PickingProductCardData();
        pickingProductCardData.setBarCode(barCode.getText());
        pickingProductCardData.setLmCode(lmCode.getText());
        pickingProductCardData.setDepartment(ParserUtil.strToInt(department.getText()));
        pickingProductCardData.setDimension(dimension.getText());
        pickingProductCardData.setTitle(title.getText());
        pickingProductCardData.setPrice(ParserUtil.strToDouble(price.getText()));
        pickingProductCardData.setWeight(ParserUtil.strToDouble(weight.getText()));
        return pickingProductCardData;
    }
}
