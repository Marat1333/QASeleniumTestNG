package com.leroy.magmobile.ui.pages.work.supply_plan.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.ShipmentProductData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class ShipmentOtherProductWidget extends CardWidget<ShipmentProductData> {
    @AppFindBy(xpath = "./*[contains(@text,'ЛМ ')]")
    Element lmCode;

    @AppFindBy(xpath = "./*[@content-desc=\"lmui-Icon\"]/following-sibling::*[1]")
    Element barCode;

    @AppFindBy(xpath = "./*[@content-desc=\"lmui-Icon\"]/following-sibling::*[2]")
    Element title;

    @AppFindBy(xpath = "./*[@text='шт.']/preceding-sibling::*[1]")
    Element expectedQuantity;

    public ShipmentOtherProductWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public ShipmentProductData collectDataFromPage(String pageSource) {
        ShipmentProductData data = new ShipmentProductData();
        data.setLmCode(ParserUtil.strWithOnlyDigits(lmCode.getText(pageSource)));
        data.setBarCode(barCode.getText(pageSource));
        data.setTitle(title.getText(pageSource));
        data.setPlannedQuantity(Integer.valueOf(expectedQuantity.getText(pageSource)));
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return lmCode.isVisible(pageSource) && expectedQuantity.isVisible(pageSource);
    }
}
