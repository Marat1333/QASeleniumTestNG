package com.leroy.magportal.api.data.userTasks;

import java.util.List;
import lombok.Data;

@Data
public class UserTasksDataList {

    private Integer completedTasksCount;
    private Integer newTasksCount;
    private List<UserTasksData> userTasks;
}