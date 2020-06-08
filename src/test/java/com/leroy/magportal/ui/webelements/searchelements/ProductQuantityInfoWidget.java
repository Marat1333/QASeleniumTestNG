package com.leroy.magportal.ui.webelements.searchelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.search.StocksData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class ProductQuantityInfoWidget extends BaseWidget {
    public ProductQuantityInfoWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//span[contains(text(),'Доступно для продажи')]/../following-sibling::*/span")
    Element availableForSaleLbl;

    @WebFindBy(xpath = ".//span[contains(text(), 'Торговый зал')]/../following-sibling::div/*")
    Element saleHallQuantityLbl;

    @WebFindBy(xpath = ".//span[contains(text(), 'Склад RM')]/../following-sibling::div[2]/*[contains(text(),'шт')]")
    Element rmWarehouseQuantityLbl;

    @WebFindBy(xpath = ".//span[contains(text(), 'Склад EM')]/../following-sibling::div[2]/*[contains(text(),'шт')]")
    Element emWarehouseQuantityLbl;

    @WebFindBy(xpath = ".//span[contains(text(), 'склад RD')]/../following-sibling::div/*")
    Element rdWarehouseQuantityLbl;

    @WebFindBy(xpath = ".//span[contains(text(), 'Недоступно')]/../following-sibling::div/*")
    Element unavailableForSaleQuantityLbl;

    public StocksData getDataFromWidget(){
        return new StocksData(Double.parseDouble(ParserUtil.strWithOnlyDigits(availableForSaleLbl.getText())),
                Integer.parseInt(ParserUtil.strWithOnlyDigits(unavailableForSaleQuantityLbl.getText())),
                Integer.parseInt(ParserUtil.strWithOnlyDigits(saleHallQuantityLbl.getText())),
                Integer.parseInt(ParserUtil.strWithOnlyDigits(rmWarehouseQuantityLbl.getText())),
                Integer.parseInt(ParserUtil.strWithOnlyDigits(emWarehouseQuantityLbl.getText())),
                Integer.parseInt(ParserUtil.strWithOnlyDigits(rdWarehouseQuantityLbl.getText())));
    }

}
