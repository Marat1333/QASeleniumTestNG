package com.leroy.magportal.api.data.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExtStocks {
    @JsonProperty("WHB")
    private Integer whb;
    @JsonProperty("WHBP")
    private Integer whbp;
    @JsonProperty("COR")
    private Integer cor;
    @JsonProperty("TSF_outbound")
    private Integer tsfOutbound;
    @JsonProperty("RTV")
    private Integer rtv;
    @JsonProperty("UTSP")
    private Integer utsp;
    @JsonProperty("TBC")
    private Integer tbc;
    @JsonProperty("EXPO")
    private Integer expo;
}