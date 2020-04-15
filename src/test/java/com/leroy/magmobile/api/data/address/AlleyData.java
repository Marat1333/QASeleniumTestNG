package com.leroy.magmobile.api.data.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlleyData {
    @JsonProperty(required = true)
    private Integer id;
    @JsonProperty(required = true)
    private Integer count;
    @JsonProperty(required = true)
    private Integer type;
    @JsonProperty(required = true)
    private Integer storeId;
    @JsonProperty(required = true)
    private Integer departmentId;
    @JsonProperty(required = true)
    private String code;

    @JsonProperty(required = false)
    private Integer productsCount;
}
