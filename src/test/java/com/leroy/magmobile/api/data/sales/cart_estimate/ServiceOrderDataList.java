package com.leroy.magmobile.api.data.sales.cart_estimate;


import lombok.Data;

import java.util.List;

@Data
public class ServiceOrderDataList {

    public ServiceOrderDataList(List<ServiceOrderData> services) {
        this.services = services;
    }

    List<ServiceOrderData> services;

}