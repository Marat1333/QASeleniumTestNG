package com.leroy.magmobile.api.data.sales.orders;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leroy.common_mashups.data.customer.CustomerData;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReqOrderData {
    private String cartId;
    private Integer documentVersion = 1;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    private LocalDateTime dateOfGiveAway;
    private List<CustomerData> customers = new ArrayList<>();
    private List<ReqOrderProductData> products = new ArrayList<>();
}
