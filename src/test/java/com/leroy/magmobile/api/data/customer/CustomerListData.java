package com.leroy.magmobile.api.data.customer;

import lombok.Data;

import java.util.List;

@Data
public class CustomerListData {
    private Integer totalCount;
    private List<CustomerData> items;
}
