package com.leroy.magmobile.api.data.supply_plan.suppliers;

import lombok.Data;

@Data
public class SupplierData {
    private String supplierId;
    private String name;
    private String phone;
    private String contactName;
    private String email;
    private String deptId;
    private String postCode;
    private String address;
    private String supplierParentId;
    private String status;
    private String type;
}
