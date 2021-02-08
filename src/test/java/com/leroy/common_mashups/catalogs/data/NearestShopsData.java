package com.leroy.common_mashups.catalogs.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NearestShopsData {

    private Integer id;
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
    private Integer buCode;
    private String buName;
    private Object availableFeatures;
    private Double price;
    private Double distance;
    private Double availableStock;
}
