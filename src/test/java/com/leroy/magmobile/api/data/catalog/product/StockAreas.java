package com.leroy.magmobile.api.data.catalog.product;

import lombok.Data;

@Data
public class StockAreas {
    private Integer EM;
    private Integer EMBuffer;
    private Integer MagOut;
    private Integer RD;
    private Integer RDBuffer;
    private Integer Reception;
    private Integer RM;
    private Integer RMBuffer;
    private Integer LS;
    private Integer SalesBuffer;
    private Integer TBC;
    private Integer TsfFromRD;
    private Integer TsfToRD;
    private Integer buffer;
}
