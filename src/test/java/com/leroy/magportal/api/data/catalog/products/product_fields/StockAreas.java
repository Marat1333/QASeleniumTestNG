package com.leroy.magportal.api.data.catalog.products.product_fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StockAreas {
    @JsonProperty("EM")
    private Integer em;
    @JsonProperty("RD")
    private Integer rd;
    @JsonProperty("RM")
    private Integer rm;
    @JsonProperty("LS")
    private Integer ls;
    private Integer buffer;

    public void replaceNull() {
        if (ls == null) ls = 0;
        if (rm == null) rm = 0;
        if (rd == null) rd = 0;
        if (em == null) em = 0;
    }
}
