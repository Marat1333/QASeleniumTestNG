package com.leroy.magmobile.ui.pages.work.ruptures.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.ruptures.RuptureCardPage;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

public class TasksListsModalPage extends CommonMagMobilePage {
    @AppFindBy(xpath = "//*[contains(@text,'Задачи по перебою')]/preceding-sibling::*")
    Button closeModalBtn;

    @AppFindBy(text = "Задачи по перебою")
    Element header;

    @AppFindBy(xpath = "//*[@text='ВОЗМОЖНЫЕ ЗАДАЧИ']/preceding-sibling::android.widget.TextView[not(contains(@text,'НУЖНО СДЕЛАТЬ')) " +
            "and not(contains(@text,'Задачи по перебою'))]")
    ElementList<Element> toDoTasksList;

    @AppFindBy(xpath = "//*[@text='ВОЗМОЖНЫЕ ЗАДАЧИ']/following-sibling::android.widget.TextView")
    ElementList<Element> possibleTasksList;

    @Override
    protected void waitForPageIsLoaded() {
        closeModalBtn.waitForVisibility();
    }

    @Step("Получить список задач 'Нужно сделать'")
    public List<String> getToDoTasks() {
        List<String> result = new ArrayList<>();
        String ps = getPageSource();
        for (Element each : toDoTasksList) {
            result.add(each.getText(ps));
        }
        return result;
    }

    @Step("Получить список задач 'Возможные задачи'")
    public List<String> getPossibleTasks() {
        List<String> result = new ArrayList<>();
        String ps = getPageSource();
        for (Element each : possibleTasksList) {
            result.add(each.getText(ps));
        }
        return result;
    }

    @Step("Закрыть модалку")
    public RuptureCardPage closeModal() {
        closeModalBtn.click();
        return new RuptureCardPage();
    }

    @Step("Выбрать задачи")
    public TasksListsModalPage selectTasks(String... taskNames) {
        for (String taskName : taskNames) {
            int previousSize = toDoTasksList.getCount();
            E(String.format("//*[@text='%s']/following-sibling::android.view.ViewGroup[@content-desc='Button-container'][1]",
                    taskName)).click();
            toDoTasksList.waitUntilSizeIsChanged(previousSize, tiny_timeout);
        }
        return this;
    }

    @Step("Проверить, что лист 'Нужно сделать' содержит указанные задачи, a лист 'Возможные задачи' не содержит")
    public TasksListsModalPage shouldToDoListContainsTaskAndPossibleListNotContainsTask(List<String> tasks) {
        List<String> toDoTasksNames = getToDoTasks();
        List<String> possibleTasksNames = getPossibleTasks();
        if (tasks.size() > 0) {
            for (String task : tasks) {
                softAssert.isTrue(toDoTasksNames.contains(task), "список задач \"Нужно сделать\" не содержит задачу " + task);
                softAssert.isTrue(!possibleTasksNames.contains(task), "список задач \"Возможные задачи\" содержит задачу " + task);
                softAssert.verifyAll();
            }
        } else {
            anAssert.isEquals(toDoTasksNames.size(), 0, "Задача в списке \"Нужно сделать\" присутствуют");
        }
        return this;
    }

    @Step("Проверить, что лист \"Нужно сделать\" не содержит указанные задачи, a лист \"Возможные задачи\" содержит")
    public TasksListsModalPage shouldToDoListNotContainsTaskAndPossibleListContainsTask(String... tasks) {
        List<String> toDoTasksNames = getToDoTasks();
        List<String> possibleTasksNames = getPossibleTasks();
        for (String task : tasks) {
            anAssert.isTrue(!toDoTasksNames.contains(task), "список задач \"Нужно сделать\" содержит задачу " + task);
            anAssert.isTrue(possibleTasksNames.contains(task), "список задач \"Возможные задачи\" не содержит задачу " + task);
        }
        return this;
    }

    @Step("Проверить, что модальное окно с экшенами отображается корректно")
    public TasksListsModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(getPageSource(), closeModalBtn, header);
        softAssert.verifyAll();
        return this;
    }
}
