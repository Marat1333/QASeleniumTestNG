package com.leroy.magmobile.api.data.catalog.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExtStocks {
    private String ItemID;
    private String StoreID;
    private Integer C3;
    @JsonProperty("RTV")
    private Integer rtv;
    @JsonProperty("WHB")
    private Integer whb;
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
    private Integer RTV_on_DC;
    private Integer TSF_outbound_confirmed;
    @JsonProperty("TSF_outbound")
    private Integer tsfOutbound;
    private Integer TSF_inbound_confirmed;
    private Integer TSF_inbound_delivered;
    private Integer BUID;
    private Integer returnReserve;
    private Integer clientsReserve;
    private Integer bufferEM;
    private Integer defectEM;
    private Integer correctionStockInWait;
    private Integer transferReserve;
    private Integer onTheWay;
}
