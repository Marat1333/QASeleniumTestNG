package com.leroy.magmobile.api.data.sales.picking;

import lombok.Data;

import java.util.List;

@Data
public class PickingTaskDataList {

    private List<PickingTaskData> items;
    private Integer totalCount;
}
