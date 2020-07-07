package com.leroy.magmobile.ui.pages.work.supply_plan.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.ShipmentCardData;
import com.leroy.utils.DateTimeUtil;
import org.openqa.selenium.WebDriver;

public class ShipmentWidget extends CardWidget<ShipmentCardData> {
    @AppFindBy(xpath = "./android.widget.TextView[1]")
    Element supplierName;

    @AppFindBy(xpath = "./android.widget.TextView[2]")
    Element supplyDate;

    @AppFindBy(xpath = "./*[@text='ожидается палет']")
    Element expectedLbl;

    @AppFindBy(xpath = "./*[@text='получено палет']")
    Element receivedLbl;

    @AppFindBy(xpath = "./android.view.ViewGroup/*")
    Element quantityLbl;

    public ShipmentWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return supplierName.isVisible() && quantityLbl.isVisible();
    }

    @Override
    public ShipmentCardData collectDataFromPage(String pageSource) {
        ShipmentCardData data = new ShipmentCardData();
        data.setName(supplierName.getText());
        data.setDateAndTime(DateTimeUtil.strToLocalDateTime(supplyDate.getText(),"d MMM, H:mm"));
        String quantity = quantityLbl.getText();
        if (quantity.contains("/")) {
            String [] tmp = quantity.split("/");
            int fact = Integer.parseInt(tmp[0]);
            int plan = Integer.parseInt(tmp[1]);
            data.setReceivedQuantity(fact);
            data.setExpectedQuantity(plan);
            data.setIsFullReceived(fact >= plan);
        }else {
            data.setReceivedQuantity(0);
            data.setExpectedQuantity(Integer.valueOf(quantity));
            data.setIsFullReceived(false);
        }
        return data;
    }
}

