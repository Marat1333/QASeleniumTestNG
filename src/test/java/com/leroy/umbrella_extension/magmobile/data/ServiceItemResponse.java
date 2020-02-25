package com.leroy.umbrella_extension.magmobile.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceItemResponse extends ResponseItem{

    private String lmCode;
    private String barCode;
    private String title;
    private String groupId;
    private String UoM;
    private Integer LMFacingPriceCoef;
    private String LMFacingUoM;
    private String LMItemType;
    private String departmentId;
    private String TaxRate;

}
