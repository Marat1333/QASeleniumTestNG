package com.leroy.magportal.api.data.picking;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PickingTaskStoragePayload extends PickingTaskWorkflowPayload {

    @JsonProperty("payload")
    private StoragePayload storagePayload;

    @Data
    public static class StoragePayload {

        private List<String> locations;
    }
}
