package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.ruptures.data.SessionData;
import com.leroy.magmobile.ui.pages.work.ruptures.widgets.ActiveSessionWidget;
import io.qameta.allure.Step;

import java.util.List;

public class SessionListPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "BackButton")
    Button backBtn;

    @AppFindBy(text = "СКАНИРОВАТЬ ПЕРЕБОИ")
    Button scanRupturesBtn;

    @AppFindBy(xpath = "//*[@text='СКАНИРОВАТЬ ПЕРЕБОИ']/ancestor::*[5]/preceding-sibling::*[1]//\tandroid.widget.TextView")
    Button choseDepartmentBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView", metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    AndroidScrollView<SessionData> activeSessionScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.widget.ScrollView/*/*/*[not(*[contains(@text,'АКТИВНЫЕ СЕССИИ')])]/descendant::*[2]",
            ActiveSessionWidget.class);

    @Override
    protected void waitForPageIsLoaded() {
        backBtn.waitForVisibility();
        scanRupturesBtn.waitForVisibility();
    }

    @Step("Выйти из сессии")
    public void exit(){
        backBtn.click();
    }

    @Step("Сканировать перебои")
    public RupturesScannerPage callScannerPage(){
        scanRupturesBtn.click();
        return new RupturesScannerPage();
    }

    @Step("Перейти в сессию с номером {sessionId}")
    public ActiveSessionPage goToSession(String sessionId) throws Exception{
        Element target = E(String.format("contains(%s)", sessionId));
        if (!target.isVisible()){
            mainScrollView.scrollDownToElement(target);
        }
        //из-за кнопки "сканировать перебои"
        mainScrollView.setSwipeDeadZonePercentage(80);
        mainScrollView.scrollDown();
        target.click();
        return new ActiveSessionPage();
    }

    @Step("Проверить, что в списке активных сессия отсутствует сессия")
    public SessionListPage shouldActiveSessionHasNotContainsSession(SessionData data) throws Exception{
        List<SessionData> uiSessionData = activeSessionScrollView.getFullDataList();
        anAssert.isFalse(uiSessionData.contains(data),"лист содержит данные");
        return this;
    }

    @Step("Проверить, что в списке активных сессия отсутствует сессия")
    public SessionListPage shouldActiveSessionContainsSession(SessionData data) throws Exception{
        List<SessionData> uiSessionData = activeSessionScrollView.getFullDataList();
        anAssert.isTrue(uiSessionData.contains(data),"лист не содержит данные");
        return this;
    }

    public SessionListPage verifyRequiredElements(){
        softAssert.areElementsVisible(getPageSource(), scanRupturesBtn, choseDepartmentBtn);
        softAssert.verifyAll();
        return this;
    }
}
