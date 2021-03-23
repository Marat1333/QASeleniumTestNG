package com.leroy.magportal.api.data.shops;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ShopData extends ShopBaseData {

    private ShopAvailableFeatureData availableFeatures;

    @Data
    private static class ShopAvailableFeatureData {

        private Boolean editDateOfDelivery;
        private Boolean editDateOfGiveaway;
        private Boolean em;
        private Boolean isWave;
        private Boolean lsrm;
        private Boolean ordersV2;
        private Boolean priceTagPrint;
    }
}
