package com.leroy.magportal.api.data.onlineOrders;

import java.util.List;
import lombok.Data;

@Data
public class RefundPayload {

    private List<Line> lines;
    private Double newDeliveryPrice;
    private String orderId;
    private String updatedBy;

    @Data
    public static class Line {
        String lineId;
        double quantityToRefund;
    }
}
