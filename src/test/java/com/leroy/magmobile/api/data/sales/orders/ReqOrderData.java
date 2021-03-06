package com.leroy.magmobile.api.data.sales.orders;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leroy.common_mashups.customer_accounts.data.CustomerData;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ReqOrderData {
    private String cartId;
    private Integer documentVersion = 1;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    private LocalDateTime dateOfGiveAway;
    private List<CustomerData> customers = new ArrayList<>();
    private List<ReqOrderProductData> products = new ArrayList<>();
    private boolean withDelivery;
    private String shopId;
}
