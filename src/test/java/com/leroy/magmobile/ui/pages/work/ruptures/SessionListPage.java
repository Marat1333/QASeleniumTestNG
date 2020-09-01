package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.more.DepartmentListPage;
import com.leroy.magmobile.ui.pages.work.ruptures.data.FinishedSessionData;
import com.leroy.magmobile.ui.pages.work.ruptures.data.SessionData;
import com.leroy.magmobile.ui.pages.work.ruptures.widgets.ActiveSessionWidget;
import com.leroy.magmobile.ui.pages.work.ruptures.widgets.FinishedSessionWidget;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

public class SessionListPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "BackButton")
    Button backBtn;

    @AppFindBy(text = "СКАНИРОВАТЬ ПЕРЕБОИ")
    Button scanRupturesBtn;

    @AppFindBy(xpath = "//*[@text='СКАНИРОВАТЬ ПЕРЕБОИ']/ancestor::*[5]/preceding-sibling::*[1]//\tandroid.widget.TextView")
    Button choseDepartmentBtn;

    @AppFindBy(text = "Сессий перебоев пока нет")
    Element noSessionsLbl;

    @AppFindBy(containsText = "Создай новую сессию,")
    Element adviceLbl;

    @AppFindBy(xpath = "//android.widget.ScrollView", metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    AndroidScrollView<SessionData> activeSessionScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//*[contains(@text, ' / ')]/following-sibling::android.view.ViewGroup[1][not(android.widget.TextView)]/..",
            ActiveSessionWidget.class);

    AndroidScrollView<FinishedSessionData> finishedSessionScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//*[contains(@text,'/') and not(contains(@text,' / '))]/../..",
            FinishedSessionWidget.class);

    @Override
    protected void waitForPageIsLoaded() {
        backBtn.waitForVisibility();
        scanRupturesBtn.waitForVisibility();
    }

    @Step("Выйти из сессии")
    public void exit() {
        backBtn.click();
    }

    @Step("Обновить страницу выполнив pull to refresh")
    public SessionListPage pullToRefresh(){
        while (!isProgressBarVisible()) {
            mainScrollView.scrollToBeginning(1);
        }
        waitUntilProgressBarIsInvisible();
        return new SessionListPage();
    }

    @Step("Сканировать перебои")
    public RupturesScannerPage callScannerPage() {
        scanRupturesBtn.click();
        return new RupturesScannerPage();
    }

    @Step("Сменить отдел")
    public SessionListPage changeDepartment(int departmentId) throws Exception {
        choseDepartmentBtn.click();
        DepartmentListPage departmentListPage = new DepartmentListPage();
        departmentListPage.selectDepartmentById("" + departmentId);
        waitUntilProgressBarIsInvisible();
        return new SessionListPage();
    }

    @Step("Открыть модалку выбора отдела")
    public DepartmentListPage callDepartmentModalPage(int departmentId) throws Exception {
        choseDepartmentBtn.click();
        return new DepartmentListPage();
    }

    @Step("Перейти в сессию с номером {sessionId}")
    public void goToSession(String sessionId) throws Exception {
        Element target = E(String.format("contains(%s)", sessionId));
        if (!target.isVisible()) {
            mainScrollView.scrollDownToElement(target);
        }
        //из-за кнопки "сканировать перебои"
        mainScrollView.setSwipeDeadZonePercentage(50);
        mainScrollView.scrollDown();
        target.click();
    }

    @Step("Проверить, что в списке активных сессия отсутствует сессия")
    public SessionListPage shouldActiveSessionHasNotContainsSession(SessionData data) throws Exception {
        List<SessionData> uiSessionData = activeSessionScrollView.getFullDataList();
        anAssert.isFalse(uiSessionData.contains(data), "лист содержит данные");
        return this;
    }

    @Step("Проверить, что в списке активных сессия отсутствует сессия")
    public SessionListPage shouldActiveSessionContainsSession(SessionData data) throws Exception {
        List<SessionData> uiSessionData = activeSessionScrollView.getFullDataList();
        anAssert.isTrue(uiSessionData.contains(data), "лист не содержит данные");
        return this;
    }

    @Step("Проверить, что все активные сессии отображены")
    public SessionListPage shouldAllActiveSessionAreVisible(List<Integer> activeSessionsIdList) throws Exception {
        List<SessionData> uiActiveSessionData = activeSessionScrollView.getFullDataList();
        anAssert.isEquals(uiActiveSessionData.size(), activeSessionsIdList.size(), "wrong session count");
        List<Integer> uiActiveSessionIdList = new ArrayList<>();
        for (SessionData each : uiActiveSessionData) {
            uiActiveSessionIdList.add(Integer.parseInt(each.getSessionNumber()));
        }
        anAssert.isTrue(uiActiveSessionIdList.containsAll(activeSessionsIdList), "there is no needed session");
        return this;
    }

    @Step("Проверить, что все активные сессии отображены")
    public SessionListPage shouldAllFinishedSessionAreVisible(List<Integer> finishedSessionsIdList) throws Exception {
        Element anchor = E("contains(ЗАВЕРШЕННЫЕ СЕССИИ)");
        if (!anchor.isVisible()) {
            //из-за холостых скроллов shouldAllActiveSessionAreVisible() приходиться подниматься
            mainScrollView.scrollUpToElement(anchor);
        }
        List<FinishedSessionData> uiFinishedSessionData = finishedSessionScrollView.getFullDataList();
        anAssert.isEquals(uiFinishedSessionData.size(), finishedSessionsIdList.size(), "wrong session count");
        List<Integer> uiFinishedSessionIdList = new ArrayList<>();
        for (SessionData each : uiFinishedSessionData) {
            uiFinishedSessionIdList.add(Integer.parseInt(each.getSessionNumber()));
        }
        anAssert.isTrue(uiFinishedSessionIdList.containsAll(finishedSessionsIdList), "there is no needed session");
        return this;
    }

    @Step("Проверить, что карточки заверешнных сессий не отображаются")
    public SessionListPage shouldFinishedSessionCardsIsNotVisible() throws Exception {
        mainScrollView.scrollToEnd();
        List<FinishedSessionData> uiFinishedSessionData = finishedSessionScrollView.getFullDataList();
        anAssert.isEquals(uiFinishedSessionData.size(), 0, "there are some finished sessions");
        return this;
    }

    @Step("Проверить, что карточки активных сессий не отображаются")
    public SessionListPage shouldActiveSessionCardsIsNotVisible() throws Exception {
        List<SessionData> uiActiveSessionData = activeSessionScrollView.getFullDataList();
        anAssert.isEquals(uiActiveSessionData.size(), 0, "there are some finished sessions");
        return this;
    }

    @Step("Проверить, что отображена надпись \"Сессий перебоев пока нет\"")
    public SessionListPage shouldNoSessionMsgLblIsVisible() {
        softAssert.areElementsVisible(getPageSource(), noSessionsLbl, adviceLbl);
        softAssert.verifyAll();
        return this;
    }

    public SessionListPage verifyRequiredElements() {
        softAssert.areElementsVisible(getPageSource(), backBtn, scanRupturesBtn, choseDepartmentBtn);
        softAssert.verifyAll();
        return this;
    }
}
