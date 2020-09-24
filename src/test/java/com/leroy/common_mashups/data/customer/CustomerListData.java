package com.leroy.common_mashups.data.customer;

import lombok.Data;

import java.util.List;

@Data
public class CustomerListData {
    private Integer totalCount;
    private List<CustomerData> items;
}
