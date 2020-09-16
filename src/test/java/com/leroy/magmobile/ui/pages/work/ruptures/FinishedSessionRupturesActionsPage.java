package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.ruptures.data.RuptureData;
import com.leroy.magmobile.ui.pages.work.ruptures.enums.Action;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.AcceptRecallFromRmModalPage;
import com.leroy.magmobile.ui.pages.work.ruptures.widgets.RuptureWidget;
import io.qameta.allure.Step;

import java.util.List;

public class FinishedSessionRupturesActionsPage extends CommonMagMobilePage {
    private static final String TYPICAL_RUPTURE_TASK_CONTAINER_XPATH = "//*[contains(@text,'%s')]/.." +
            "/following-sibling::*[@content-desc='Button-container']//android.widget.TextView[contains(@text,'%s')]";

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

    @AppFindBy(text = "Сделать отзыв с RM")
    Button recallFromRm;

    @AppFindBy(text = "Создана заявка на отзыв")
    Element recallRequestHasBeenCreatedMsgLbl;

    AndroidScrollView<RuptureData> ruptureCardScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, "./*/android.view.ViewGroup[android.view.ViewGroup]/descendant::*[3]",
            RuptureWidget.class);

    @Override
    protected void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
        completedAllActionsRatioLbl.waitForVisibility();
        backBtn.waitForVisibility();
    }

    @Step("Нажать на кнопку \"Сделать отзыв с RM\"")
    public AcceptRecallFromRmModalPage recallProductFromRm(){
        recallFromRm.click();
        return new AcceptRecallFromRmModalPage();
    }

    @Step("Перейти на страницу выполненных задач")
    public FinishedSessionRupturesActionsPage goToDoneTasks() {
        doneTasksBtn.click();
        return new FinishedSessionRupturesActionsPage();
    }

    @Step("Проскроллиться к началу прокручиваемой области")
    public FinishedSessionRupturesActionsPage scrollToBeginning(){
        ruptureCardScrollView.scrollToBeginning();
        return this;
    }

    @Step("Перейти назад")
    public void goBack() {
        backBtn.click();
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

    @Step("Нажать на чек-бокс задачи для перебоя с лм кодом {lmCode}")
    public FinishedSessionRupturesActionsPage choseTaskCheckBoxForProduct(Action action, String lmCode) throws Exception {
        Element el = E(String.format(TYPICAL_RUPTURE_TASK_CONTAINER_XPATH, lmCode, action.getActionName()));
        if (!el.isVisible()){
            ruptureCardScrollView.scrollDownToElement(el);
        }
        ruptureCardScrollView.scrollDown();
        if (!el.isVisible()){
            ruptureCardScrollView.scrollUpToElement(el);
        }
        el.click();
        el.waitForInvisibility();
        return this;
    }

    @Step("Проверить, что заголовок содержит действие {actionName}")
    public FinishedSessionRupturesActionsPage shouldHeaderContainsActionName(String actionName) {
        anAssert.isElementTextContains(headerLbl, actionName);
        return this;
    }

    @Step("Проверить, что отображается сообщение о созданной заявке на отзыв с RM")
    public FinishedSessionRupturesActionsPage shouldRecallRequestHasBeenCreatedMsgIsVisible(){
        anAssert.isElementVisible(recallRequestHasBeenCreatedMsgLbl);
        return this;
    }

    @Step("Проверить, что по задаче не осталось перебоев")
    public FinishedSessionRupturesActionsPage shouldAllRuptureTaskHaveDone() {
        shouldNoActiveRuptureTasksAreAvailable();
        shouldTasksRatioCounterIsCorrect(0);
        return this;
    }

    @Step("Проверить, что отображается надпись \"перебои отработаны\"")
    public FinishedSessionRupturesActionsPage shouldNoActiveRuptureTasksAreAvailable() {
        anAssert.isElementVisible(noTasksLbl);
        return this;
    }

    @Step("Проверить, что счетчик задач отображает корректное значение")
    public FinishedSessionRupturesActionsPage shouldTasksRatioCounterIsCorrect(int counter) {
        anAssert.isElementTextEqual(completedAllActionsRatioLbl, String.valueOf(counter));
        return this;
    }

    @Step("Проверить, что счетчик задач отображает корректное значение")
    public FinishedSessionRupturesActionsPage shouldTasksRatioCounterIsCorrect(int doneTasksCounter, int allTasks) {
        String[] completedAllActionsRatio = completedAllActionsRatioLbl.getText().split("/");
        softAssert.isEquals(completedAllActionsRatio[0], String.valueOf(doneTasksCounter), "done tasks count");
        softAssert.isEquals(completedAllActionsRatio[1], String.valueOf(allTasks), "all tasks count");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что счетчик выполненных задач отображает корректное значение")
    public FinishedSessionRupturesActionsPage shouldDoneTasksCounterIsCorrect(int counter) {
        anAssert.isElementTextEqual(doneTasksBtn, String.valueOf(counter));
        return this;
    }

    @Step("Проверить, что страница имеет вид отображения \"Выполненные задачи\"")
    public FinishedSessionRupturesActionsPage shouldDoneTasksViewIsPresented() {
        anAssert.isElementTextEqual(headerLbl, "Выполненные задачи");
        return this;
    }

    @Step("Проверить, что перебои отображены корректно")
    public FinishedSessionRupturesActionsPage shouldRuptureDataIsCorrect(RuptureData... data) throws Exception {
        List<RuptureData> uiRuptureData = ruptureCardScrollView.getFullDataList();
        for (int i = 0; i < data.length; i++) {
            softAssert.isEquals(uiRuptureData.get(i), data[i], "data mismatch");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что отображается указанное кол-во перебоев")
    public FinishedSessionRupturesActionsPage shouldRuptureCountIsCorrect(int ruptureCount) throws Exception {
        if (ruptureCount==0){
            shouldNoActiveRuptureTasksAreAvailable();
        }else {
            List<RuptureData> uiRuptureData = ruptureCardScrollView.getFullDataList();
            anAssert.isEquals(uiRuptureData.size(), ruptureCount, "wrong ruptures quantity");
        }
        return this;
    }

    @Step("Проверить, что карточка перебоя не содержит задачи")
    public FinishedSessionRupturesActionsPage shouldRuptureCardHasNotContainsTask(String lmCode, Action action) {
        anAssert.isFalse(E(String.format(TYPICAL_RUPTURE_TASK_CONTAINER_XPATH, lmCode, action.getActionName())).isVisible(),
                "Element is visible");
        return this;
    }

    @Step("Проверить, что отображается задача \"сделать отзыв с RM\"")
    public FinishedSessionRupturesActionsPage shouldRecallFromRmTaskIsVisible(){
        anAssert.isElementVisible(recallFromRm);
        return this;
    }

    public FinishedSessionRupturesActionsPage verifyRequiredElements() {
        softAssert.areElementsVisible(backBtn, headerLbl, completedAllActionsRatioLbl);
        softAssert.verifyAll();
        return this;
    }
}
