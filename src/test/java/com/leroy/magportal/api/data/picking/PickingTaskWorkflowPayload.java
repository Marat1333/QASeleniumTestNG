package com.leroy.magportal.api.data.picking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leroy.magportal.api.data.onlineOrders.OrderProductDataPayload;
import java.util.List;
import lombok.Data;

@Data
public class PickingTaskWorkflowPayload {

    private String action;
    @JsonProperty("payload")
    private WorkflowPayload workflowPayload;

    @Data
    public static class WorkflowPayload {

        private List<OrderProductDataPayload> products;
    }
}
