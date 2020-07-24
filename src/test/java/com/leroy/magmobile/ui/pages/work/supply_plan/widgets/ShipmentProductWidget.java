package com.leroy.magmobile.ui.pages.work.supply_plan.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.ShipmentProductData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public class ShipmentProductWidget extends CardWidget<ShipmentProductData> {
    @AppFindBy(xpath = "./*[@content-desc='lmCode']")
    Element lmCode;

    @AppFindBy(xpath = "./*[@content-desc='barCode']")
    Element barCode;

    @AppFindBy(xpath = "./*[@content-desc='title']")
    Element title;

    @AppFindBy(xpath = "./*[@content-desc='receivedQuantity']")
    Element receivedQuantity;

    @AppFindBy(xpath = "./*[@content-desc='expectedQuantity']")
    Element expectedQuantity;

    public ShipmentProductWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public ShipmentProductData collectDataFromPage(String pageSource) {
        ShipmentProductData data = new ShipmentProductData();
        data.setLmCode(ParserUtil.strWithOnlyDigits(lmCode.getText()));
        data.setBarCode(barCode.getText());
        data.setTitle(title.getText());
        try {
            data.setReceivedQuantity(Integer.valueOf(receivedQuantity.getText()));
        }catch (NoSuchElementException e){
            Log.warn("product wasn`t received");
        }
        data.setPlannedQuantity(Integer.valueOf(expectedQuantity.getText()));
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return lmCode.isVisible() && expectedQuantity.isVisible();
    }
}
