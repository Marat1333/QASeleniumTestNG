package com.leroy.magportal.api.data.onlineOrders;

import lombok.Data;

@Data
public class GagarinStoreInfo {

    private Integer regionId;
    private Integer storeId;
    private Double longitude;
    private Double latitude;
    private Boolean isRef;
}
