package com.leroy.magportal.api.data.userTasks;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leroy.magportal.api.constants.UserTasksProject;
import com.leroy.magportal.api.constants.UserTasksStatus;
import com.leroy.magportal.api.constants.UserTasksType;
import lombok.Data;

@Data
public class UserTasksData {

    private String comment;
    private String createdBy;
    private String creatorName;
    private String creatorSurname;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]['Z']")
    private String createdOn;
    private String decision;
    private String dueDate;
    private Boolean needToDo;
    private RelatedObject relatedObject;
    private String responsible;
    private UserTasksProject source;
    private UserTasksStatus status;
    private String title;
    private UserTasksType type;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]['Z']")
    private String updatedOn;
    private String updaterName;
    private String updaterSurname;
    private String userTaskId;
    private Integer version;

    @Data
    private static class RelatedObject {

        private String id;
        private String type = "SOLUTION";
    }
}