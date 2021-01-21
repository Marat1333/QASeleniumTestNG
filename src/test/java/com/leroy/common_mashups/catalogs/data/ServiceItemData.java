package com.leroy.common_mashups.catalogs.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ServiceItemData {

    private String lmCode;
    private String barCode;
    private String title;
    private String groupId;
    @JsonProperty("UoM")
    private String uom;
    @JsonProperty("LMFacingPriceCoef")
    private Integer lmFacingPriceCoef;
    @JsonProperty("LMFacingUoM")
    private String lmFacingUoM;
    @JsonProperty("LMItemType")
    private String lmItemType;
    private Integer departmentId;
    @JsonProperty("TaxRate")
    private String taxRate;

}
