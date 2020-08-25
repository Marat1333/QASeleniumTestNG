package com.leroy.magmobile.ui.pages.work.ruptures.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class TaskWidget extends Element {
    public TaskWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./*/android.widget.TextView[1]")
    Element taskNameLbl;

    @AppFindBy(xpath = "./*/*/android.widget.TextView")
    Element finishedAllTasksRatioLbl;
}
