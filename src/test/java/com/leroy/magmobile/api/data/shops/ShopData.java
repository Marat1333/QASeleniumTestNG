package com.leroy.magmobile.api.data.shops;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShopData {
    private String id;
    private String name;
    private String address;
    private String phone;
    private Integer regionId;
    private String regionName;
    private Double lat;
    @JsonProperty("long")
    private Double longitude;
    private String timezone;
    private String cityName;
    private String regionKladr;
    private String cityKladr;
    private ShopAvailableFeatureData availableFeatures;
    private Double distance;
    private PriceAndStockData priceAndStock;
}
