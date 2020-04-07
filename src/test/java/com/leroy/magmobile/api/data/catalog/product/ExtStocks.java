package com.leroy.magmobile.api.data.catalog.product;

import lombok.Data;
import scala.Int;

@Data
public class ExtStocks {
    private String ItemID;
    private String StoreID;
    private Integer C3;
    private Integer RTV;
    private Integer WHB;
    private Integer EXPO;
    private Integer COR;
    private Integer WHBP;
    private Integer UTSP;
    private Integer TBC;
    private Integer RTV_on_DC;
    private Integer TSF_outbound_confirmed;
    private Integer TSF_inbound_confirmed;
    private Integer TSF_inbound_delivered;
    private Integer BUID;
    private Integer returnReserve;
    private Integer expo;
    private Integer clientsReserve;
    private Integer bufferEM;
    private Integer defectEM;
    private Integer correctionStockInWait;
    private Integer transferReserve;
    private Integer onTheWay;
}
