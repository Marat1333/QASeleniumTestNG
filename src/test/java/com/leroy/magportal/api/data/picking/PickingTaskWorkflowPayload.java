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
    @JsonProperty("payload")
    private StoragePayload storagePayload;

    @Data
    public static class WorkflowPayload {

        private List<OrderProductDataPayload> products;
    }

    @Data
    public static class StoragePayload {

        private List<String> locations;
    }
}
