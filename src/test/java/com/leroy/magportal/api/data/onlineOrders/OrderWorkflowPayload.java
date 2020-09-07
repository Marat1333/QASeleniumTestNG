package com.leroy.magportal.api.data.onlineOrders;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class OrderWorkflowPayload {

    private String action;
    @JsonProperty("payload")
    private WorkflowPayload workflowPayload;

    @Data
    public static class WorkflowPayload {

        private List<OrderProductDataPayload> products;
    }
}
