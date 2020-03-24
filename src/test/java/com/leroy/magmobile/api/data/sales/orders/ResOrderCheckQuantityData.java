package com.leroy.magmobile.api.data.sales.orders;

import lombok.Data;

import java.util.List;

@Data
public class ResOrderCheckQuantityData {
    private String result;
    private String groupingId;
    private List<ResOrderProductCheckQuantityData> products;
}
