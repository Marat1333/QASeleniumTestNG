package com.leroy.magmobile.api.data.sales;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.ServiceOrderData;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
public class SalesDocumentResponseData {

    @JsonProperty(required = true)
    @Pattern(regexp = "\\d+")
    private String docId;

    @JsonProperty(required = true)
    @Pattern(regexp = "\\d+")
    private String fullDocId;

    private String docType;
    private String shopId;

    @JsonProperty(required = true)
    private String salesDocStatus;
    private String pinCode;
    private String comment;
    private List<CartEstimateProductOrderData> products = new ArrayList<>();
    private List<ServiceOrderData> services = new ArrayList<>();
    private Double docPriceSum;
    private GiveAway giveAway;
    private String newServiceId;

    @Data
    public static class GiveAway {
        private String point;
    }

}
