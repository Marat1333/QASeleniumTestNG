package com.leroy.umbrella_extension.magmobile.data.sales.transfer;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TransferSalesDocData {

    private String taskId;
    private String status;
    private String shopId;
    private String createdBy;
    private String createdDate;
    private String pointOfGiveAway;
    private LocalDate dateOfGiveAway;
    private String departmentId;
    private List<TransferProductOrderData> products;
    private String z;

}
