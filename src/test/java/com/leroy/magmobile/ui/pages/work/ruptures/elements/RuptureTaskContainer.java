package com.leroy.magmobile.ui.pages.work.ruptures.elements;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.TasksListsModalPage;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class RuptureTaskContainer extends Element {
    public RuptureTaskContainer(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    private final static String TYPICAL_CHECKBOX_XPATH = "//*[@text='%s']/preceding-sibling::android.view.ViewGroup";

    @AppFindBy(xpath = ".//*[@text='Назначить задачи']")
    Button appointTaskBtn;

    @AppFindBy(xpath = ".//*[@text='Сделать отзыв с RM']")
    Element recallFromRmLbl;

    @AppFindBy(xpath = ".//*[@content-desc='Button-container']//android.widget.TextView[not(contains(@text,'Назначить задачи'))]")
    ElementList<Element> tasksList;

    public List<String> getTaskList() {
        String ps = getPageSource();
        List<String> result = new ArrayList<>();
        for (Element each : tasksList) {
            result.add(each.getText(ps));
        }
        if (recallFromRmLbl.isVisible(ps)) {
            result.add(recallFromRmLbl.getText(ps));
        }
        return result;
    }

    public void setCheckBoxesToTasks(String taskName) {
        E(String.format(TYPICAL_CHECKBOX_XPATH,
                taskName)).click();
    }

    public TasksListsModalPage callActionsModalPage() throws Exception {
        if (appointTaskBtn.isVisible()) {
            appointTaskBtn.click();
        } else {
            this.findChildElement(".//android.view.ViewGroup[@content-desc='Button-container'][2]").click();
        }
        return new TasksListsModalPage();
    }

    public boolean getCheckBoxCondition(String taskName) throws Exception {
        RuptureTaskContainerGreyCheckBox taskCheckBox = E(String.format(TYPICAL_CHECKBOX_XPATH, taskName),
                RuptureTaskContainerGreyCheckBox.class);
        return taskCheckBox.isChecked();
    }

}
