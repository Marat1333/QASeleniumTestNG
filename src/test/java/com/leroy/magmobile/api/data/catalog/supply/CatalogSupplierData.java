package com.leroy.magmobile.api.data.catalog.supply;

import lombok.Data;

import java.util.List;

@Data
public class CatalogSupplierData {
    private Integer storeId;
    private String code;
    private String name;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String contractDeliveryTime;
    private String planningDeliveryTime;
    private String nextOrderDate;
    private String status;
    private String deliveryType;
    private String franko;
    private String packSize;
    private String deliveryMethod;
    private List<SupplyHistoryData> history;
}
