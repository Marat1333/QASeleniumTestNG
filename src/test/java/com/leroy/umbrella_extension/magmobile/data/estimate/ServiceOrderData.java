package com.leroy.umbrella_extension.magmobile.data.estimate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceOrderData extends ServiceItemResponse {

    public ServiceOrderData() {
        this.quantity = 1.0;
    }

    public ServiceOrderData(ServiceItemResponse serviceItemResponse) {
        this();
        setLmCode(serviceItemResponse.getLmCode());
        setTitle(serviceItemResponse.getTitle());
        setBarCode(serviceItemResponse.getBarCode());
        setUoM(serviceItemResponse.getUoM());
        setGroupId(serviceItemResponse.getGroupId());
    }

    private String id;
    private Double quantity;
    private Double price;
    private Double priceSum;
    private Boolean updated;

}
