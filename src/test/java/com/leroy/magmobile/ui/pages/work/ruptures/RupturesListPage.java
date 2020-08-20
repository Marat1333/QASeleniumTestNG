package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.ruptures.data.ActiveSessionData;
import com.leroy.magmobile.ui.pages.work.ruptures.data.RuptureData;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.DeleteSessionModalPage;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.ExitActiveSessionModalPage;
import com.leroy.magmobile.ui.pages.work.ruptures.widgets.RuptureWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.util.List;

public class RupturesListPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "Button")
    Button backBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button\"]/../following-sibling::android.view.ViewGroup[1]")
    Button deleteBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button\"]/../following-sibling::android.widget.TextView[1]")
    Element creationDateLbl;

    @AppFindBy(containsText = " перебо")
    Element creatorAndRuptureQuantityLbl;

    @AppFindBy(containsText = "Сессия №")
    Element sessionNumberAndStatusLbl;

    @AppFindBy(text = "ПЕРЕБОЙ")
    Button addRuptureBtn;

    @AppFindBy(text = "ЗАВЕРШИТЬ")
    Button endSessionBtn;

    AndroidScrollView<RuptureData> ruptureCardScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, "./*/android.view.ViewGroup[android.view.ViewGroup]/descendant::*[3]",
            RuptureWidget.class);

    @Override
    protected void waitForPageIsLoaded() {
        creationDateLbl.waitForVisibility();
        sessionNumberAndStatusLbl.waitForVisibility();
    }

    public ActiveSessionData getActiveSessionData() {
        String ps = getPageSource();
        ActiveSessionData data = new ActiveSessionData();
        data.setCreateDate(creationDateLbl.getText(ps));
        String[] tmpArray = creatorAndRuptureQuantityLbl.getText(ps).split(" / ");
        data.setRuptureQuantity(Integer.parseInt(ParserUtil.strWithOnlyDigits(tmpArray[0])));
        data.setCreatorName(tmpArray[1]);
        tmpArray = sessionNumberAndStatusLbl.getText(ps).split(" ");
        data.setSessionNumber(ParserUtil.strWithOnlyDigits(tmpArray[1]));
        return data;
    }

    @Step("Добавить перебой в сессию")
    public RupturesScannerPage addRuptureToSession(){
        addRuptureBtn.click();
        return new RupturesScannerPage();
    }

    @Step("Удалить сессию")
    public DeleteSessionModalPage deleteSession(){
        deleteBtn.click();
        return new DeleteSessionModalPage();
    }

    @Step("Открыть перебой {ruptureLm}")
    public RuptureCardPage goToRuptureCard(String ruptureLm) throws Exception {
        Element target = E(String.format("contains(%s)", ruptureLm));
        if (!target.isVisible()) {
            ruptureCardScrollView.scrollDownToElement(target);
        }
        //из-за кнопок "+перебой и завершить"
        ruptureCardScrollView.scrollDown();
        target.click();
        return new RuptureCardPage();
    }

    @Step("Нажать на кнопку назад")
    public ExitActiveSessionModalPage exitActiveSession() {
        backBtn.click();
        return new ExitActiveSessionModalPage();
    }

    @Step("Проверить, что перебоя нет в списке")
    public RupturesListPage shouldRuptureIsNotInList(String lmCode) throws Exception {
        if (!ruptureCardScrollView.isVisible()) {
            return this;
        } else {
            List<RuptureData> uiRuptureDataList = ruptureCardScrollView.getFullDataList();
            for (int i = 0; i < uiRuptureDataList.size(); i++) {
                anAssert.isFalse(uiRuptureDataList.get(i).getLmCode().contains(lmCode), "rupture " + lmCode + " is in the list");
            }
        }
        return this;
    }

    @Step("Проверить, что данные перебоев отображены корректно")
    public RupturesListPage shouldRupturesDataIsCorrect(RuptureData... dataArray) throws Exception {
        List<RuptureData> uiRuptureDataList = ruptureCardScrollView.getFullDataList();
        for (int i = 0; i < dataArray.length; i++) {
            anAssert.isEquals(uiRuptureDataList.get(i), dataArray[i], "data mismatch");
        }
        return this;
    }

    @Step("Проверить, что счетчик перебоев отображает корректное значение")
    public RupturesListPage shouldRuptureCounterIsCorrect(int counter){
        String[] tmpArray = creatorAndRuptureQuantityLbl.getText().split(" / ");
        int uiCounter = Integer.parseInt(ParserUtil.strWithOnlyDigits(tmpArray[0]));
        anAssert.isEquals(uiCounter, counter, "ruptures counter");
        return this;
    }

    public RupturesListPage verifyRequiredElements() {
        softAssert.areElementsVisible(backBtn, deleteBtn, creationDateLbl, creatorAndRuptureQuantityLbl,
                sessionNumberAndStatusLbl, addRuptureBtn, endSessionBtn);
        softAssert.verifyAll();
        return this;
    }
}
