package com.leroy.magmobile.ui.pages.work.ruptures.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.ruptures.data.FinishedSessionData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class FinishedSessionWidget extends CardWidget<FinishedSessionData> {

    public FinishedSessionWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(containsText = "№")
    Element sessionNumberLbl;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='date']")
    Element creationAndFinishingDateLbl;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='quantityLbl']/android.widget.TextView")
    Element quantityLbl;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='creator']")
    Element creatorLbl;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='sessionType']")
    Element type;

    @Override
    public FinishedSessionData collectDataFromPage(String pageSource) {
        FinishedSessionData data = new FinishedSessionData();
        data.setSessionNumber(ParserUtil.strWithOnlyDigits(sessionNumberLbl.getText(pageSource)));
        data.setCreateDate(creationAndFinishingDateLbl.getText(pageSource).split("—")[0]);
        data.setFinishDate(creationAndFinishingDateLbl.getText(pageSource).split("—")[1]);
        data.setCreatorName(creatorLbl.getText(pageSource));

        if (!type.isVisible(pageSource)) {
            String[] quantity = quantityLbl.getText(pageSource).split("/");
            data.setRuptureQuantity(ParserUtil.strToInt(quantity[1]));
            data.setFinishedRuptureQuantity(ParserUtil.strToInt(quantity[0]));
            data.setType("Standard");
        }
        else {
            data.setRuptureQuantity(ParserUtil.strToInt(quantityLbl.getText(pageSource)));
            data.setType("Bulk");
        }
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return sessionNumberLbl.isVisible(pageSource) && creatorLbl.isVisible(pageSource);
    }
}
