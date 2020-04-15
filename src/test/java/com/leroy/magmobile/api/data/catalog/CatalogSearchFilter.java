package com.leroy.magmobile.api.data.catalog;

import lombok.Data;

@Data
public class CatalogSearchFilter {
    private Boolean hasAvailableStock;
    private Boolean topEM;
    private Boolean bestPrice;
    private Boolean top1000;
    private Boolean avs;
}
