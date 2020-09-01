package com.leroy.magportal.api.data.shops;

import lombok.Data;

@Data
public class ShopAvailableFeatureData {
    private Boolean editDateOfDelivery;
    private Boolean editDateOfGiveaway;
    private Boolean em;
    private Boolean isWave;
    private Boolean lsrm;
    private Boolean ordersV2;
    private Boolean priceTagPrint;//TODO: Extend
}
