package com.leroy.magportal.api.data.picking;

import java.util.List;
import lombok.Data;

@Data
public class OrdersPickingTasksDataList {

    private List<PickingTaskData> pickingTasks;
    private String orderId;
}
