package com.leroy.magportal.ui.models.salesdoc;


import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import lombok.Data;

import java.util.List;

@Data
public class SalesDocWebData {
    private String number;
    private String status;
    private String creationDate;
    private String authorName;
    private SimpleCustomerData client;
    private List<OrderWebData> orders;
}
