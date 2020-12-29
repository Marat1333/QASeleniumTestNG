package com.leroy.magmobile.ui.pages.work.ruptures.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskData {
    private String taskName;
    private int doneTasksCount;
    private int allTasksCount;
}
