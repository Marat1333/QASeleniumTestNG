package com.leroy.magportal.api.data.picking;

import lombok.Data;

import java.util.List;

@Data
public class PickingTaskDataList {
    private List<PickingTaskData> items;
}
