package com.leroy.umbrella_extension.magmobile.data.sales.orders;

import com.leroy.umbrella_extension.magmobile.data.customer.CustomerData;
import com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate.ProductOrderData;
import lombok.Data;

import java.util.List;

@Data
public class OrderData {

    private String orderId;
    private Integer shopId;
    private String createdAt;
    private String createdBy;
    private String channel;
    private String paymentStatus;
    private String comment;
    private List<CustomerData> customers;
    private List<ProductOrderData> products; // TODO нужен другой ProductOrderData точно
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
    private Object giveAway;
    private String fulfillmentTaskId;
    private Integer fulfillmentVersion;
    private String status;
    private String creatorName;
    private String creatorSurname;

}
