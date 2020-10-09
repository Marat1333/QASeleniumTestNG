package com.leroy.magmobile.ui.pages.work.ruptures.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.ruptures.data.SessionData;
import com.leroy.magmobile.ui.pages.work.ruptures.data.FinishedSessionData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class ActiveSessionWidget extends CardWidget<SessionData> {

    public ActiveSessionWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(containsText = "№")
    Element sessionNumberLbl;

    @AppFindBy(xpath = "./android.widget.TextView[1]")
    Element creationDateLbl;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[contains(@text, '№')]]/following-sibling::android.view.ViewGroup/android.widget.TextView")
    Element quantityLbl;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[contains(@text, '№')]]/following-sibling::android.widget.TextView[2]")
    Element creatorLbl;

    @Override
    public SessionData collectDataFromPage(String pageSource) {
        SessionData data = new SessionData();
        data.setSessionNumber(ParserUtil.strWithOnlyDigits(sessionNumberLbl.getText(pageSource)));
        data.setCreateDate(creationDateLbl.getText(pageSource));
        data.setRuptureQuantity(ParserUtil.strToInt(quantityLbl.getText(pageSource)));
        data.setCreatorName(creatorLbl.getText(pageSource));
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return sessionNumberLbl.isVisible(pageSource) && quantityLbl.isVisible(pageSource);
    }
}
