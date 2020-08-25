package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.work.ruptures.widgets.TaskWidget;

public class FinishedSessionPage extends SessionPage {
    @AppFindBy(xpath = "//android.widget.ScrollView/*/*", clazz = TaskWidget.class)
    ElementList<TaskWidget> taskWidgets;
}
