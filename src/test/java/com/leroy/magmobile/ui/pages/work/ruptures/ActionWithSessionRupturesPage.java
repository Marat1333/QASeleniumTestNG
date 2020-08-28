package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.ruptures.data.RuptureData;
import com.leroy.magmobile.ui.pages.work.ruptures.widgets.RuptureWidget;
import io.qameta.allure.Step;

import java.util.List;

public class ActionWithSessionRupturesPage extends CommonMagMobilePage {
    @AppFindBy(xpath = "//*[@content-desc='DefaultScreenHeader']/android.view.ViewGroup[1]")
    Button backBtn;

    @AppFindBy(xpath = "//*[@content-desc='DefaultScreenHeader']/android.view.ViewGroup[2]/*")
    Element completedAllActionsRatioLbl;

    @AppFindBy(xpath = "//*[@content-desc='DefaultScreenHeader']/android.widget.TextView")
    Element headerLbl;

    @AppFindBy(text = "Перебои отработаны!")
    Element noTasksLbl;

    @AppFindBy(xpath = "//*[contains(@text,'Выполненные задачи')]/following-sibling::*[1]/*")
    Button doneTasksBtn;

    AndroidScrollView<RuptureData> ruptureCardScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, "./*/android.view.ViewGroup[android.view.ViewGroup]/descendant::*[3]",
            RuptureWidget.class);

    @Override
    protected void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
        completedAllActionsRatioLbl.waitForVisibility();
    }

    @Step("Перейти на страницу выполненных задач")
    public ActionWithSessionRupturesPage goToDoneTasks() {
        doneTasksBtn.click();
        return new ActionWithSessionRupturesPage();
    }

    @Step("Перейти назад")
    public FinishedSessionPage goBack() {
        backBtn.click();
        return new FinishedSessionPage();
    }

    @Step("Открыть перебой {ruptureLm}")
    public RuptureCardPage goToRuptureCard(String ruptureLm) throws Exception {
        Element target = E(String.format("contains(%s)", ruptureLm));
        if (!target.isVisible()) {
            ruptureCardScrollView.scrollDownToElement(target);
        }
        target.click();
        return new RuptureCardPage();
    }

    @Step("Проверить, что заголовок содержит действие {actionName}")
    public ActionWithSessionRupturesPage shouldHeaderContainsActionName(String actionName) {
        anAssert.isElementTextContains(headerLbl, actionName);
        return this;
    }

    @Step("Проверить, что по задаче не осталось перебоев")
    public ActionWithSessionRupturesPage shouldAllRuptureTaskHaveDone() {
        shouldNoActiveRuptureTasksAreAvailable();
        shouldTasksRatioCounterIsCorrect(0);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что отображается надпись \"перебои отработаны\"")
    public ActionWithSessionRupturesPage shouldNoActiveRuptureTasksAreAvailable() {
        anAssert.isElementVisible(noTasksLbl);
        return this;
    }

    @Step("Проверить, что счетчик задач отображает корректное значение")
    public ActionWithSessionRupturesPage shouldTasksRatioCounterIsCorrect(int counter) {
        anAssert.isElementTextEqual(completedAllActionsRatioLbl, String.valueOf(counter));
        return this;
    }

    @Step("Проверить, что счетчик задач отображает корректное значение")
    public ActionWithSessionRupturesPage shouldTasksRatioCounterIsCorrect(int doneTasksCounter, int allTasks) {
        String[] completedAllActionsRatio = completedAllActionsRatioLbl.getText().split("/");
        softAssert.isEquals(completedAllActionsRatio[0], String.valueOf(doneTasksCounter), "done tasks count");
        softAssert.isEquals(completedAllActionsRatio[1], String.valueOf(allTasks), "all tasks count");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что счетчик выполненных задач отображает корректное значение")
    public ActionWithSessionRupturesPage shouldDoneTasksCounterIsCorrect(int counter) {
        anAssert.isElementTextEqual(doneTasksBtn, String.valueOf(counter));
        return this;
    }

    @Step("Проверить, что страница имеет вид отображения \"Выполненные задачи\"")
    public ActionWithSessionRupturesPage shouldDoneTasksViewIsPresented() {
        anAssert.isElementTextEqual(headerLbl, "Выполненные задачи");
        return this;
    }

    @Step("Проверить, что перебои отображены корректно")
    public ActionWithSessionRupturesPage shouldRuptureDataIsCorrect(RuptureData... data) throws Exception {
        List<RuptureData> uiRuptureData = ruptureCardScrollView.getFullDataList();
        for (int i = 0; i < data.length; i++) {
            softAssert.isEquals(uiRuptureData.get(i), data[i], "data mismatch");
        }
        softAssert.verifyAll();
        return this;
    }

    public ActionWithSessionRupturesPage verifyRequiredElements() {
        softAssert.areElementsVisible(backBtn, headerLbl, completedAllActionsRatioLbl);
        softAssert.verifyAll();
        return this;
    }
}
