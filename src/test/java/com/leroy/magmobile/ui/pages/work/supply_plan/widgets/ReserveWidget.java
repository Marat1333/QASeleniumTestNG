package com.leroy.magmobile.ui.pages.work.supply_plan.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.AppointmentCardData;
import com.leroy.utils.DateTimeUtil;
import org.openqa.selenium.WebDriver;

public class ReserveWidget extends CardWidget<AppointmentCardData> {
    @AppFindBy(xpath = "./android.widget.TextView[1]")
    Element supplierName;

    @AppFindBy(xpath = "./android.widget.TextView[2]")
    Element supplyDate;

    public ReserveWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public AppointmentCardData collectDataFromPage(String pageSource) {
        AppointmentCardData data = new AppointmentCardData();
        data.setDateAndTime(DateTimeUtil.strToLocalDateTime(supplyDate.getText(), "d MMM, H:mm"));
        data.setName(supplierName.getText());
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return supplierName.isVisible() && supplyDate.isVisible();
    }
}

