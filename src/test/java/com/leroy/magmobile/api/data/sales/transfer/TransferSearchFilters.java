package com.leroy.magmobile.api.data.sales.transfer;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TransferSearchFilters {

    private String status;
    private String createdBy;

}
