package com.leroy.magportal.api.data.picking;

import com.fasterxml.jackson.annotation.JsonProperty;
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

        private List<ProductDataPayload> products;
    }

    @Data
    public static class StoragePayload {

        private List<String> locations;
    }

    @Data
    public static class ProductDataPayload {

        private String lineId;
        private String lmCode;
        private Double quantity;
        private String reason;
    }
}
