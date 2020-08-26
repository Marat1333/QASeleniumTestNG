package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.ruptures.data.SessionData;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public abstract class SessionPage extends CommonMagMobilePage {
    @AppFindBy(xpath = "//android.view.ViewGroup[contains(@content-desc,\"Button\")]")
    protected Button backBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[contains(@content-desc,\"Button\")]/../following-sibling::android.widget.TextView[1]")
    protected Element creationDateLbl;

    @AppFindBy(containsText = " перебо")
    protected Element creatorAndRuptureQuantityLbl;

    @AppFindBy(containsText = "Сессия №")
    protected Element sessionNumberAndStatusLbl;

    @Override
    protected void waitForPageIsLoaded() {
        creationDateLbl.waitForVisibility();
        sessionNumberAndStatusLbl.waitForVisibility();
    }

    public SessionData getSessionData() {
        String ps = getPageSource();
        SessionData data = new SessionData();
        data.setCreateDate(creationDateLbl.getText(ps));
        String[] tmpArray = creatorAndRuptureQuantityLbl.getText(ps).split(" / ");
        data.setRuptureQuantity(Integer.parseInt(ParserUtil.strWithOnlyDigits(tmpArray[0])));
        data.setCreatorName(tmpArray[1]);
        tmpArray = sessionNumberAndStatusLbl.getText(ps).split(" ");
        data.setSessionNumber(ParserUtil.strWithOnlyDigits(tmpArray[1]));
        return data;
    }

    @Step("Проверить, что счетчик перебоев отображает корректное значение")
    public SessionPage shouldRuptureCounterIsCorrect(int counter) {
        String[] tmpArray = creatorAndRuptureQuantityLbl.getText().split(" / ");
        int uiCounter = Integer.parseInt(ParserUtil.strWithOnlyDigits(tmpArray[0]));
        anAssert.isEquals(uiCounter, counter, "ruptures counter");
        return this;
    }

    public void verifyRequiredElements() {
        softAssert.areElementsVisible(backBtn, creationDateLbl, creatorAndRuptureQuantityLbl,
                sessionNumberAndStatusLbl);
        softAssert.verifyAll();
    }
}
