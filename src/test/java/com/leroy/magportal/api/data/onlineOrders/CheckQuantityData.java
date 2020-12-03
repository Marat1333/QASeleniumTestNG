package com.leroy.magportal.api.data.onlineOrders;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leroy.magmobile.api.data.sales.BaseProductOrderData;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CheckQuantityData {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    private LocalDateTime dateOfGiveAway;
    private List<BaseProductOrderData> products;
}
