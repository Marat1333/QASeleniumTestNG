package com.leroy.magmobile.api.data.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class CatalogSearchFilter {
    private Boolean hasAvailableStock;
    private Boolean topEM;
    private Boolean bestPrice;
    private Boolean top1000;
    private Boolean avs;
    private String lmCode;
    private String departmentId;
}
