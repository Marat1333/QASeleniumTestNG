package com.leroy.magmobile.api.data.sales.orders;

import com.leroy.magportal.api.data.onlineOrders.ShipToData;
import java.util.List;
import lombok.Data;

@Data
public class OrderData {

    private String fullDocId;
    private String orderId;
    private String docType;
    private String salesDocStatus;
    private String status;
    private String shopId;
    private String createdAt;
    private String createdBy;
    private String channel;
    private String paymentStatus;
    private String comment;
    private List<OrderCustomerData> customers;
    private List<OrderProductData> products;
    private List<String> userComments;
    private Integer solutionVersion;
    private Boolean delivery;
    private String paymentType;
    private String paymentTaskId;
    private Integer paymentVersion;
    private List<Object> payments;
    private List<Object> refunds;
    private Object registerStatus;
    private String pinCode;
    private GiveAwayData giveAway;
    private String fulfillmentTaskId;
    private Integer fulfillmentVersion;
    private String creatorName;
    private String creatorSurname;


    // For Update, Confirm requests
    private String priority;

    public void increasePaymentVersion() {
        paymentVersion++;
    }

    public void increaseFulfillmentVersion() {
        fulfillmentVersion++;
    }

    public void increaseFulfillmentVersion(int val) {
        fulfillmentVersion += val;
    }
}
