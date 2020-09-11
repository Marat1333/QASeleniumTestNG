package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.magmobile.ui.pages.work.ruptures.data.TaskData;
import com.leroy.magmobile.ui.pages.work.ruptures.enums.Action;
import com.leroy.magmobile.ui.pages.work.ruptures.widgets.TaskWidget;
import io.qameta.allure.Step;

import java.util.List;

public class FinishedSessionPage extends SessionPage {
    AndroidScrollView<TaskData> taskScrollView = new AndroidScrollView<>(driver, AndroidScrollView.TYPICAL_LOCATOR,
            "./*/*/*[not(descendant::android.widget.TextView[@text='Все задачи'])]", TaskWidget.class);

    @AppFindBy(xpath = "//*[@text='Все задачи']/..")
    TaskWidget allTasksWidget;

    @Override
    protected void waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        waitUntilProgressBarIsInvisible();
    }

    @Step("Перейти назад")
    public SessionListPage exitFinishedSession() {
        backBtn.click();
        return new SessionListPage();
    }

    @Step("Перейти на страницу действия с руптюрами в сессии")
    public FinishedSessionRupturesActionsPage goToActionPage(Action action) {
        E(String.format("contains(%s)", action.getActionName())).click();
        return new FinishedSessionRupturesActionsPage();
    }

    @Step("Проверить, что отображается корректное кол-во действий и у них есть счетчики")
    public FinishedSessionPage shouldTasksCountIsCorrect(int count) throws Exception {
        List<TaskData> tasksList = taskScrollView.getFullDataList();
        anAssert.isEquals(tasksList.size(), count, "wrong quantity");
        for (TaskData each : tasksList) {
            softAssert.isTrue(each.getDoneTasksCount() >= 0, "wrong done tasks count");
            softAssert.isTrue(each.getAllTasksCount() >= 0, "wrong all tasks count");
        }
        softAssert.verifyAll();
        if (count > 0) {
            anAssert.isElementVisible(allTasksWidget);
        }
        return this;
    }

    @Step("Проверить, что статус сессии изменился на \"завершен\"")
    public FinishedSessionPage shouldStatusIsFinished() {
        anAssert.isElementTextContains(sessionNumberAndStatusLbl, "(завершена)");
        return this;
    }

    @Step("Проверить, что данные по задачам отображены корректно")
    public FinishedSessionPage shouldTasksAreCorrect(TaskData... dataArray) throws Exception {
        List<TaskData> uiTaskDataList = taskScrollView.getFullDataList();
        for (TaskData each : dataArray) {
            anAssert.isTrue(uiTaskDataList.contains(each), "expected data not found");
        }
        return this;
    }

    @Step("Проверить, что счетчик по всем задачам отображает корректные значения")
    public FinishedSessionPage shouldAllTasksCounterIsCorrect(TaskData allTasksCounterData) {
        anAssert.isEquals(allTasksWidget.collectDataFromPage(), allTasksCounterData, "wrong counter value");
        return this;
    }
}
