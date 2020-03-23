package com.leroy.magmobile.api.data.sales.orders;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leroy.magmobile.api.data.customer.CustomerData;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostOrderData {
    private String cartId;
    private Integer documentVersion = 1;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    private LocalDateTime dateOfGiveAway;
    private List<CustomerData> customers = new ArrayList<>();
    private List<PostOrderProductData> products = new ArrayList<>();
}
