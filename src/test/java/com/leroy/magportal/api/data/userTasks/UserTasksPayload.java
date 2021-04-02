package com.leroy.magportal.api.data.userTasks;

import com.leroy.magportal.api.constants.UserTasksType;
import lombok.Data;

@Data
public class UserTasksPayload {

    private String ldap;
    private Boolean needToDo;
    private String orderId;
    private UserTasksType taskType;
    private String text;
    private Integer version;
}