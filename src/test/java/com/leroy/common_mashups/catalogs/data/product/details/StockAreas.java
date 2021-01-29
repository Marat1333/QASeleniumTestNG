package com.leroy.common_mashups.catalogs.data.product.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StockAreas {
    @JsonProperty("EM")
    private Integer em;//TODO All stocks are DOUBLE
    private Integer EMBuffer;
    private Integer MagOut;
    @JsonProperty("RD")
    private Integer rd;
    private Integer RDBuffer;
    private Integer Reception;
    @JsonProperty("RM")
    private Integer rm;
    private Integer RMBuffer;
    @JsonProperty("LS")
    private Integer ls;
    private Integer SalesBuffer;
    private Integer TBC;
    private Integer TsfFromRD;
    private Integer TsfToRD;
    private Integer buffer;
    private CalculatedStocks calculatedStocks;

    public void replaceNull() {
        if (ls == null) {
            ls = 0;
        }
        if (rm == null) {
            rm = 0;
        }
        if (rd == null) {
            rd = 0;
        }
        if (em == null) {
            em = 0;
        }
    }

    @Data
    private class CalculatedStocks {

    }
}
