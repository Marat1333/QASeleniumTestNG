package com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate;

import com.leroy.umbrella_extension.magmobile.data.catalog.ServiceItemData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class ServiceOrderData extends ServiceItemData {

    public ServiceOrderData() {
        this.quantity = 1.0;
    }

    public ServiceOrderData(ServiceItemData serviceItemResponse) {
        this();
        setLmCode(serviceItemResponse.getLmCode());
        setTitle(serviceItemResponse.getTitle());
        setBarCode(serviceItemResponse.getBarCode());
        setUom(serviceItemResponse.getUom());
        setGroupId(serviceItemResponse.getGroupId());
    }

    private String id;
    private Double quantity;
    private Double price;
    private Double priceSum;
    private Boolean updated;

}
