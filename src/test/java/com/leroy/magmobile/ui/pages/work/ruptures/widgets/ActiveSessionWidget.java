package com.leroy.magmobile.ui.pages.work.ruptures.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.ruptures.data.SessionData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class ActiveSessionWidget extends CardWidget<SessionData> {

    public ActiveSessionWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(containsText = "№")
    Element sessionNumberLbl;

    @AppFindBy(accessibilityId = "date")
    Element creationDateLbl;

//    @AppFindBy(accessibilityId = "quantityLbl")
    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='quantityLbl']/android.widget.TextView")
    Element quantityLbl;

    @AppFindBy(accessibilityId = "creator")
    Element creatorLbl;

    @AppFindBy(accessibilityId = "type")
    Element type;

    @Override
    public SessionData collectDataFromPage(String pageSource) {
        SessionData data = new SessionData();
        data.setSessionNumber(ParserUtil.strWithOnlyDigits(sessionNumberLbl.getText(pageSource)));
        data.setCreateDate(creationDateLbl.getText(pageSource).split("Открыта ")[1]);
        data.setRuptureQuantity(ParserUtil.strToInt(quantityLbl.getText(pageSource)));
        data.setCreatorName(creatorLbl.getText(pageSource));
        data.setType(type.isVisible() == false ? "Standard" : "Bulk");
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return sessionNumberLbl.isVisible(pageSource) && quantityLbl.isVisible(pageSource);
    }
}
