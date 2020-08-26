package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionsModalPage extends CommonMagMobilePage {
    @AppFindBy(xpath = "//*[contains(@text,'Задачи по перебою')]/preceding-sibling::*")
    Button closeModalBtn;

    @AppFindBy(xpath = "//*[@text='ВОЗМОЖНЫЕ ЗАДАЧИ']/preceding-sibling::android.widget.TextView[not(contains(@text,'НУЖНО СДЕЛАТЬ')) " +
            "and not(contains(@text,'Задачи по перебою'))]")
    ElementList<Element> toDoTasksList;

    @AppFindBy(xpath = "//*[@text='ВОЗМОЖНЫЕ ЗАДАЧИ']/following-sibling::android.widget.TextView")
    ElementList<Element> possibleTasksList;

    @Override
    protected void waitForPageIsLoaded() {
        closeModalBtn.waitForVisibility();
    }

    private Map<List<String>, List<String>> getTasksList() {
        String ps = getPageSource();
        Map<List<String>, List<String>> result = new HashMap<>();
        List<String> toDoTasksNames = new ArrayList<>();
        List<String> possibleTasksNames = new ArrayList<>();
        for (Element eachTask : toDoTasksList) {
            toDoTasksNames.add(eachTask.getText(ps));
        }
        for (Element eachTask : possibleTasksList) {
            possibleTasksNames.add(eachTask.getText(ps));
        }
        result.put(toDoTasksNames, possibleTasksNames);
        return result;
    }

    public List<String> getToDoTasks() {
        List<String> result = new ArrayList<>();
        String ps = getPageSource();
        for (Element each : toDoTasksList) {
            result.add(each.getText(ps));
        }
        return result;
    }

    public List<String> getPossibleTasks() {
        List<String> result = new ArrayList<>();
        String ps = getPageSource();
        for (Element each : possibleTasksList) {
            result.add(each.getText(ps));
        }
        return result;
    }

    @Step("Закрыть модалку")
    public RuptureCardPage closeModal(){
        closeModalBtn.click();
        return new RuptureCardPage();
    }

    @Step("Выбрать задачу")
    public ActionsModalPage choseTasks(String... taskNames) {
        for (String taskName : taskNames) {
            E(String.format("//*[@text='%s']/following-sibling::android.view.ViewGroup[@content-desc='Button-container'][1]",
                    taskName)).click();
            toDoTasksList.waitUntilSizeHasChanged();
        }
        return this;
    }

    public ActionsModalPage shouldToDoListContainsTaskAndPossibleListNotContainsTask(List<String> tasks) {
        String [] array = new String[tasks.size()];
        return shouldToDoListContainsTaskAndPossibleListNotContainsTask(array);
    }

    @Step("Проверить, что лист \"Нужно сделать\" содержит указанные задачи, a лист \"Возможные задачи\" не содержит")
    public ActionsModalPage shouldToDoListContainsTaskAndPossibleListNotContainsTask(String...tasks) {
        Map<List<String>, List<String>> tasksMap = getTasksList();
        List<String> toDoTasksNames = null;
        List<String> possibleTasksNames = null;
        for (Map.Entry<List<String>, List<String>> entry : tasksMap.entrySet()) {
            toDoTasksNames = entry.getKey();
            possibleTasksNames = entry.getValue();
        }
        if (tasks.length>0) {
            for (String task : tasks) {
                anAssert.isTrue(toDoTasksNames.contains(task), "список задач \"Нужно сделать\" не содержит задачу " + task);
                anAssert.isTrue(!possibleTasksNames.contains(task), "список задач \"Возможные задачи\" содержит задачу " + task);
            }
        }else {
            anAssert.isEquals(toDoTasksNames.size(), 0,"Задача в списке \"Нужно сделать\" присутствуют");
        }
        return this;
    }

    @Step("Проверить, что лист \"Нужно сделать\" не содержит указанные задачи, a лист \"Возможные задачи\" содержит")
    public ActionsModalPage shouldToDoListNotContainsTaskAndPossibleListContainsTask(String...tasks) {
        Map<List<String>, List<String>> tasksMap = getTasksList();
        List<String> toDoTasksNames = null;
        List<String> possibleTasksNames = null;
        for (Map.Entry<List<String>, List<String>> entry : tasksMap.entrySet()) {
            toDoTasksNames = entry.getKey();
            possibleTasksNames = entry.getValue();
        }
        for (String task : tasks) {
            anAssert.isTrue(!toDoTasksNames.contains(task), "список задач \"Нужно сделать\" содержит задачу " + task);
            anAssert.isTrue(possibleTasksNames.contains(task), "список задач \"Возможные задачи\" не содержит задачу " + task);
        }
        return this;
    }
}
