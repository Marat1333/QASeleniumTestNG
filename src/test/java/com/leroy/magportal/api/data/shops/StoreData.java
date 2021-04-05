package com.leroy.magportal.api.data.shops;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leroy.magportal.api.constants.ShopProductsEnum.Constants;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StoreData extends ShopBaseData {

    private StoreAvailableFeatureData availableFeatures;

    @Data
    private static class StoreAvailableFeatureData {

        @JsonProperty(Constants.MM)
        private List<String> mm;
        @JsonProperty(Constants.CO)
        private List<String> co;
        @JsonProperty(Constants.EQ)
        private List<String> eq;
        @JsonProperty(Constants.CP)
        private List<String> cp;
    }
}
