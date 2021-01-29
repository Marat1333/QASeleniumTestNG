package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.more.DepartmentListPage;
import com.leroy.magmobile.ui.pages.work.ruptures.data.FinishedSessionData;
import com.leroy.magmobile.ui.pages.work.ruptures.data.SessionData;
import com.leroy.magmobile.ui.pages.work.ruptures.widgets.ActiveSessionWidget;
import com.leroy.magmobile.ui.pages.work.ruptures.widgets.FinishedSessionWidget;
import io.qameta.allure.Step;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionListPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "BackButton")
    Button backBtn;

    @AppFindBy(text = "ПО ОДНОМУ")
    Button scanRupturesByOneBtn;

    @AppFindBy(text = "МАССОВО")
    Button scanRupturesBulkBtn;

    @AppFindBy(xpath = "//*[@text='ПО ОДНОМУ']/ancestor::*[5]/preceding-sibling::*[1]//android.widget.TextView")
    Button choseDepartmentBtn;

    @AppFindBy(text = "Сессий перебоев пока нет")
    Element noSessionsLbl;

    @AppFindBy(containsText = "Создай новую сессию,")
    Element adviceLbl;

    @AppFindBy(xpath = "//android.widget.ScrollView", metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    AndroidScrollView<SessionData> activeSessionScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.view.ViewGroup[@content-desc='activeSession']",
            ActiveSessionWidget.class);

    AndroidScrollView<FinishedSessionData> finishedSessionScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.view.ViewGroup[@content-desc='finishedSession']",
            FinishedSessionWidget.class);

    @Override
    protected void waitForPageIsLoaded() {
        backBtn.waitForVisibility();
        scanRupturesByOneBtn.waitForVisibility();
        scanRupturesBulkBtn.waitForVisibility();
        waitUntilProgressBarIsInvisible();
    }

    @Step("Выйти из сессии")
    public void exit() {
        backBtn.click();
    }

    @Step("Обновить страницу выполнив pull to refresh")
    public SessionListPage pullToRefresh() {
        mainScrollView.scrollToBeginning();
        mainScrollView.scrollToBeginning();
        waitUntilProgressBarIsInvisible();
        return new SessionListPage();
    }

    @Step("Сканировать перебои 'по одному'")
    public RupturesScannerPage clickScanRupturesByOneButton() {
        scanRupturesByOneBtn.click();
        return new RupturesScannerPage();
    }

    @Step("Сканировать перебои 'массово'")
    public RupturesScannerPage clickScanRupturesBulkButton() {
        scanRupturesBulkBtn.click();
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
        CardWidget<String> cardWidget =
                mainScrollView.searchForWidgetByText(sessionId);
        anAssert.isTrue(cardWidget.collectDataFromPage().contains(sessionId),
                "Не нашли сессию №" + sessionId);
        //из-за кнопки "сканировать перебои"
        if (cardWidget.getLocation().getY() / (double) driver.manage().window().getSize().getHeight() * 100 > 90) {
            mainScrollView.setSwipeDeadZonePercentage(30);
            mainScrollView.scrollDown();
        }
        cardWidget.click();
    }

    @Step("Проверить, что в списке активных сессий отсутствует сессия")
    public SessionListPage shouldActiveSessionsHaveNotContainSession(SessionData data) throws Exception {
        List<SessionData> uiSessionData = activeSessionScrollView.getFullDataList();
        anAssert.isFalse(uiSessionData.contains(data), "лист содержит данные");
        return this;
    }

    @Step("Проверить, что в списке активных сессий отсутствует сессия")
    public SessionListPage shouldActiveSessionsHaveNotContainSession(String sessionId) throws Exception {
        List<SessionData> uiSessionData = activeSessionScrollView.getFullDataList();
        List<String> sessionIds = uiSessionData.stream().map(SessionData::getSessionNumber).collect(Collectors.toList());
        anAssert.isFalse(sessionIds.contains(sessionId), "лист содержит данные");
        return this;
    }

    @Step("Проверить, что в списке активных сессий присутствует сессия")
    public SessionListPage shouldActiveSessionsContainSession(SessionData data) throws Exception {
        List<SessionData> uiSessionData = activeSessionScrollView.getFullDataList();
        anAssert.isTrue(uiSessionData.contains(data), "лист не содержит данные");
        return this;
    }

    @Step("Проверить, что в списке активных сессий присутствует сессия")
    public SessionListPage shouldActiveSessionsContainSession(String sessionId) throws Exception {
        List<SessionData> uiSessionData = activeSessionScrollView.getFullDataList();
        List<String> sessionIds = uiSessionData.stream().map(SessionData::getSessionNumber).collect(Collectors.toList());
        anAssert.isTrue(sessionIds.contains(sessionId), "лист не содержит данные");
        return this;
    }

    @Step("Проверить, что в списке завршенных сессий отсутствует сессия")
    public SessionListPage shouldFinishedSessionsHaveNotContainSession(String sessionId) throws Exception {
        List<FinishedSessionData> uiSessionData = finishedSessionScrollView.getFullDataList();
        List<String> sessionIds = uiSessionData.stream().map(SessionData::getSessionNumber).collect(Collectors.toList());
        anAssert.isFalse(sessionIds.contains(sessionId), "лист содержит данные");
        return this;
    }

    @Step("Проверить, что в списке завршенных сессий присутствует сессия")
    public SessionListPage shouldFinishedSessionsContainSession(String sessionId) throws Exception {
        List<FinishedSessionData> uiSessionData = finishedSessionScrollView.getFullDataList();
        List<String> sessionIds = uiSessionData.stream().map(SessionData::getSessionNumber).collect(Collectors.toList());
        anAssert.isTrue(sessionIds.contains(sessionId), "лист не содержит данные");
        return this;
    }

    @Step("Проверить, что все активные сессии отображены")
    public SessionListPage shouldTheseActiveSessionsArePresent(List<Integer> sessionsIdList) throws Exception {
        List<SessionData> uiActiveSessionData = activeSessionScrollView.getFullDataList();
        anAssert.isEquals(uiActiveSessionData.size(), sessionsIdList.size(), "Неверное кол-во сессий");
        List<Integer> uiActiveSessionIdList = new ArrayList<>();
        for (SessionData each : uiActiveSessionData) {
            uiActiveSessionIdList.add(Integer.parseInt(each.getSessionNumber()));
        }
        anAssert.isEquals(uiActiveSessionIdList, sessionsIdList, "Ожидались другие номера сессий");
        return this;
    }

    @Step("Проверить, что все завершенные сессии отображены")
    public SessionListPage shouldTheseFinishedSessionsArePresent(List<Integer> finishedSessionsIdList) throws Exception {
        Element anchor = E("contains(ЗАВЕРШЕННЫЕ СЕССИИ)");
        if (!anchor.isVisible()) {
            //из-за холостых скроллов shouldAllActiveSessionAreVisible() приходиться подниматься
            mainScrollView.scrollUpToElement(anchor);
        }
        List<FinishedSessionData> uiFinishedSessionData = finishedSessionScrollView.getFullDataList();
        anAssert.isEquals(uiFinishedSessionData.size(), finishedSessionsIdList.size(), "Неверное кол-во сессий");
        List<Integer> uiFinishedSessionIdList = new ArrayList<>();
        for (SessionData each : uiFinishedSessionData) {
            uiFinishedSessionIdList.add(Integer.parseInt(each.getSessionNumber()));
        }
        anAssert.isEquals(uiFinishedSessionIdList, finishedSessionsIdList, "Ожидались другие номера сессий");
        return this;
    }

    @Step("Проверить, что карточки заверешнных сессий не отображаются")
    public SessionListPage shouldFinishedSessionCardsAreNotVisible() throws Exception {
        mainScrollView.scrollToBeginning();
        int actualSize = finishedSessionScrollView.getRowCount();
        anAssert.isEquals(actualSize, 0, "найдены несколько завершенных сессий");
        return this;
    }

    @Step("Проверить, что карточки активных сессий не отображаются")
    public SessionListPage shouldActiveSessionCardsAreNotVisible() throws Exception {
        activeSessionScrollView.scrollToBeginning();
        int actualSize = activeSessionScrollView.getRowCount();
        anAssert.isEquals(actualSize, 0, "найдены несколько активных сессий");
        return this;
    }

    @Step("Проверить, что отображена надпись \"Сессий перебоев пока нет\"")
    public SessionListPage shouldNoSessionMsgLblIsVisible() {
        softAssert.areElementsVisible(getPageSource(), noSessionsLbl, adviceLbl);
        softAssert.verifyAll();
        return this;
    }

    public SessionListPage verifyRequiredElements() {
        softAssert.areElementsVisible(getPageSource(), backBtn, scanRupturesByOneBtn, choseDepartmentBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить данные последней массовой сессии")
    public void verifyLastBulkSessionData(int expectedRupturesCount) throws Exception {
        List<SessionData> uiSessionData = activeSessionScrollView.getFullDataList(1);
        SessionData sessionData = uiSessionData.get(0);
        softAssert.isEquals(sessionData.getRuptureQuantity(), expectedRupturesCount, "Количество перебоев не совпадает");
        // softAssert.isEquals(sessionData.getType(), "Bulk", "Сессия не массовая"); TODO переделать после выполнения RUP-374
        softAssert.verifyAll();
    }

    @Step("Проверить данные активной массовой сессии по sessionId")
    public void verifyActiveBulkSessionDataBySessionId(int expectedRupturesCount, int sessionId) throws Exception {
        List<SessionData> uiSessionData = activeSessionScrollView.getFullDataList();

        for(SessionData session : uiSessionData){
            if(Integer.parseInt(session.getSessionNumber()) == sessionId) {
                softAssert.isEquals(session.getRuptureQuantity(), expectedRupturesCount, "Количество перебоев не совпадает");
                // softAssert.isEquals(sessionData.getType(), "Bulk", "Сессия не массовая"); TODO переделать после выполнения RUP-374
                softAssert.verifyAll();
                return;
            }
            Assert.fail("Не нашли сессию №" + sessionId);
        }
    }

    @Step("Проверить тост успешного завершения сессии")
    public void checkSuccessFinishBulkSessionToast() {
        anAssert.isTrue(driver.getPageSource().contains("Сессия успешно завершена"), "Некорректный текст тоста");
    }

    @Step("Проверить тост при нажатии на завершенную массовую сессию")
    public void checkFinishedBulkSessionToast() {
        anAssert.isTrue(driver.getPageSource().contains("Сессия массового сканирования доступна только в Report"),
                "Некорректный текст тоста");
    }
}
