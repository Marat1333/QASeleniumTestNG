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

    @AppFindBy(accessibilityId = "date")
    Element creationDateLbl;

    @AppFindBy(xpath = ".//android.view.ViewGroup[android.widget.TextView[contains(@text, '№')]]/following-sibling::android.view.ViewGroup/android.widget.TextView")
    Element quantityLbl;

    @AppFindBy(xpath = ".//android.view.ViewGroup[android.widget.TextView[contains(@text, '№')]]/following-sibling::android.widget.TextView[2]")
    Element creatorLbl;

    public FinishedSessionWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public FinishedSessionData collectDataFromPage(String pageSource) {
        FinishedSessionData data = new FinishedSessionData();
        data.setSessionNumber(ParserUtil.strWithOnlyDigits(sessionNumberLbl.getText(pageSource)));
        data.setCreateDate(creationDateLbl.getText(pageSource));
        String[] quantity = quantityLbl.getText(pageSource).split("/");
        //data.setRuptureQuantity(ParserUtil.strToInt(quantity[0]));
        data.setCreatorName(creatorLbl.getText(pageSource));
        data.setCreatedTaskQuantity(ParserUtil.strToInt(quantity[0]));
        data.setFinishedTaskQuantity(ParserUtil.strToInt(quantity[1]));
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return sessionNumberLbl.isVisible(pageSource) && creatorLbl.isVisible(pageSource);
    }
}
