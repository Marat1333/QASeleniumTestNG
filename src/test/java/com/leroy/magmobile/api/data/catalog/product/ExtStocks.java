package com.leroy.magmobile.api.data.catalog.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExtStocks {
    @JsonProperty("WHB")
    private Integer whb;
    @JsonProperty("RTV")
    private Integer rtv;
    @JsonProperty("EXPO")
    private Integer expo;
    @JsonProperty("COR")
    private Integer cor;
    @JsonProperty("WHBP")
    private Integer whbp;
    @JsonProperty("UTSP")
    private Integer utsp;
    @JsonProperty("TBC")
    private Integer tbc;
    @JsonProperty("TSF_outbound")
    private Integer tsfOutbound;
    private Integer returnReserve;
    private Integer clientsReserve;
    private Integer bufferEM;
    private Integer defectEM;
    private Integer correctionStockInWait;
    private Integer transferReserve;
    private Integer onTheWay;
}
