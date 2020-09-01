package com.leroy.magportal.api.data.shops;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leroy.magmobile.api.data.shops.ShopAvailableFeatureData;
import lombok.Data;

@Data
public class ShopData {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String regionId;
    private String regionName;
    private Double lat;
    @JsonProperty("long")
    private Double longitude;
    private String timezone;
    private String cityName;
    private String regionKladr;
    private String cityKladr;
    private ShopAvailableFeatureData availableFeatures;
    private Integer buCode;
    private String buName;
}
