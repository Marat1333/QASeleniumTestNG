package com.leroy.magmobile.api.data.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CatalogSearchFilter {
    private Boolean hasAvailableStock;
    private Boolean topEM;
    private Boolean bestPrice;
    private Boolean top1000;
    private Boolean avs;
}
