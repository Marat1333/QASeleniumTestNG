package com.leroy.magportal.api.data.onlineOrders;

import com.leroy.magmobile.api.data.sales.BaseProductOrderData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderProductDataPayload extends BaseProductOrderData {

    private String reason;
    private String type = null;
}
