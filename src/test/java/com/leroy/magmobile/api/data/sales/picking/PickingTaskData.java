package com.leroy.magmobile.api.data.sales.picking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PickingTaskData {
    private String taskId;
    private Integer shopId;
    private Boolean delivery;
    private String taskStatus;
    private String pointOfGiveAway;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'")
    private LocalDateTime dateOfGiveAway;
    private Integer documentVersion;
    private Integer buId;
    private String pickingZone;
    private String priority;
    private String employeeId;
    private List<Object> linkedObjects;
    private Object creationInformation;
    private Object lastUpdateInformation;
    private List<String> departmentId;
    private List<String> departments;
    private Double totalWeight;
    private Double maxSize;
    private List<Object> products;
}