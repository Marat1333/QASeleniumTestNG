package com.leroy.magmobile.ui.pages.sales.product_card.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.sales.product_card.data.SupplyHistoryData;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class SupplyHistoryWidget extends CardWidget<SupplyHistoryData> {

    @AppFindBy(xpath = "./*[1]")
    Element id;

    @AppFindBy(xpath = "./*[2]")
    Element orderedReceivedAmount;

    @AppFindBy(xpath = "./*[3]")
    Element contractDate;

    @AppFindBy(xpath = "./*[4]")
    Element notesDate;

    @AppFindBy(xpath = "./*[5]")
    Element receiveDate;

    @AppFindBy(xpath = "./*[8]")
    Element anchorLbl;

    public SupplyHistoryWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public SupplyHistoryData collectDataFromPage() {
        return collectDataFromPage(getPageSource());
    }

    @Override
    public SupplyHistoryData collectDataFromPage(String pageSource) {
        String dateFormat = "dd.MM.yy";
        SupplyHistoryData data = new SupplyHistoryData();
        data.setId(ParserUtil.strWithOnlyDigits(id.getText(pageSource)));
        String[] orderedReceivedNumbersArray = orderedReceivedAmount.getText(pageSource).split("/");
        data.setOrderedAmount(ParserUtil.strWithOnlyDigits(orderedReceivedNumbersArray[0]));
        data.setReceivedAmount(ParserUtil.strWithOnlyDigits(orderedReceivedNumbersArray[1]));
        data.setContractDate(DateTimeUtil.strToLocalDate(contractDate.getText(pageSource), dateFormat));
        data.setNoteDate(DateTimeUtil.strToLocalDate(notesDate.getText(pageSource), dateFormat));
        data.setReceiveDate(DateTimeUtil.strToLocalDate(receiveDate.getText(pageSource), dateFormat));
        return data;
    }

    @Override
    public boolean isFullyVisible() {
        return isFullyVisible(getPageSource());
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return id.isVisible(pageSource) && anchorLbl.isVisible(pageSource);
    }
}
