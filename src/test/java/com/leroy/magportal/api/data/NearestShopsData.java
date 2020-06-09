package com.leroy.magportal.api.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NearestShopsData {
    Integer id;
    String name;
    String address;
    String phone;
    Integer regionId;
    String regionName;
    Double lat;
    @JsonProperty("long")
    Double longg;
    String timezone;
    String cityName;
    String regionKladr;
    String cityKladr;
    Integer buCode;
    String buName;
    Object availableFeatures;
    Double distance;
    String price;
    String priceUnit;
    String priceCurrency;
    Double availableStock;
}
