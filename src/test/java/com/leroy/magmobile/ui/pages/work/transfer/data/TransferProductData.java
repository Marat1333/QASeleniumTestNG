package com.leroy.magmobile.ui.pages.work.transfer.data;

import lombok.Data;

@Data
public class TransferProductData {
    private String lmCode;
    private String title;
    private String barCode;
    private Integer availableStock;
    private Integer selectedQuantity;
}
