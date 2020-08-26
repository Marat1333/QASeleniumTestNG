package com.leroy.magmobile.ui.pages.work.ruptures.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.ruptures.data.TaskData;
import org.openqa.selenium.WebDriver;

public class TaskWidget extends CardWidget<TaskData> {
    public TaskWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./*/android.widget.TextView[1]")
    Element taskNameLbl;

    @AppFindBy(xpath = "./*/*/android.widget.TextView")
    Element finishedAllTasksRatioLbl;

    @Override
    public TaskData collectDataFromPage(String pageSource) throws Exception {
        TaskData data = new TaskData();
        data.setTaskName(taskNameLbl.getText(pageSource));
        String[] tmp = finishedAllTasksRatioLbl.getText(pageSource).split("/");
        data.setDoneTasksCount(Integer.parseInt(tmp[0]));
        data.setAllTasksCount(Integer.parseInt(tmp[1]));
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return taskNameLbl.isVisible(pageSource);
    }
}
