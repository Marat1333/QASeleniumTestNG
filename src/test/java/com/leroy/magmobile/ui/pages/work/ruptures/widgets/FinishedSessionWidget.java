package com.leroy.magmobile.ui.pages.work.ruptures.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.ruptures.data.FinishedSessionData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class FinishedSessionWidget extends CardWidget<FinishedSessionData> {
    @AppFindBy(containsText = "№")
    Element sessionNumberLbl;

    @AppFindBy(xpath = "./android.widget.TextView[1]")
    Element creationDateLbl;

    @AppFindBy(containsText = " перебо")
    Element quantityAndCreatorLbl;

    @AppFindBy(xpath = "./*[contains(@text,' перебо')]/following-sibling::*[1]/*")
    Element finishedCreatedTasksRatioLbl;

    public FinishedSessionWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public FinishedSessionData collectDataFromPage(String pageSource) {
        FinishedSessionData data = new FinishedSessionData();
        data.setSessionNumber(ParserUtil.strWithOnlyDigits(sessionNumberLbl.getText(pageSource)));
        data.setCreateDate(creationDateLbl.getText(pageSource));
        String[] tmpArray = quantityAndCreatorLbl.getText(pageSource).split(" / ");
        data.setRuptureQuantity(Integer.parseInt(ParserUtil.strWithOnlyDigits(tmpArray[0])));
        data.setCreatorName(tmpArray[1]);
        tmpArray = finishedCreatedTasksRatioLbl.getText(pageSource).split("/");
        data.setFinishedTaskQuantity(Integer.parseInt(tmpArray[0]));
        data.setCreatedTaskQuantity(Integer.parseInt(tmpArray[1]));
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return sessionNumberLbl.isVisible(pageSource) && quantityAndCreatorLbl.isVisible(pageSource);
    }
}
