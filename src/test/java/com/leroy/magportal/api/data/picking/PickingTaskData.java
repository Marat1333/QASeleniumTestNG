package com.leroy.magportal.api.data.picking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class PickingTaskData {

    private String taskId;
    private String orderId;
    private String channel;
    private String customerType;
    private String customerName;
    private String customerSurname;
    private Integer shopId;
    private Boolean delivery;
    private String taskStatus;
    private String extOrderId;
    private String pointOfGiveAway;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]['Z']")
    private LocalDateTime dateOfGiveAway;
    private Integer documentVersion;
    private String pickingZone;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]['Z']")
    private LocalDateTime pickingDeadline;
    private Integer buId;
    private String priority;
    private Boolean offline;
    private List<LinkedObject> linkedObjects;
    private List<String> departmentId;
    private List<String> departments;
    @JsonProperty("products")
    private List<ProductData> productData;
    private Double totalWeight;
    private Double maxSize;
    private String employeeName;
    private String paymentStatus;
    private String paymentType;

    @Data
    public static class ProductData {

        private String title;
        private String lmCode;
        private String lineId;
        private String lineStatus;
        private Integer orderedQuantity;
        private Integer confirmedQuantity;
        private Integer assignedQuantity;
        private List<SourceData> source;

        @Data
        public static class SourceData {

            private Integer assignedItems;
            private String type;
        }
    }

    @Data
    public static class LinkedObject {

        private String objectType;
        private String objectId;
        private String objectStatus;
        private List<ObjectLine> objectLines;

        @Data
        public static class ObjectLine {

            private String objectLineId;
            private String taskLineId;
        }
    }
}