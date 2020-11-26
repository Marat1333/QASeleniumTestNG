package com.leroy.common_mashups.customer_accounts.data;

import lombok.Data;

import java.util.List;

@Data
public class CustomerListData {
    private Integer totalCount;
    private List<CustomerData> items;
}
