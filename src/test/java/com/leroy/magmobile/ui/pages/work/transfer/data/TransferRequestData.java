package com.leroy.magmobile.ui.pages.work.transfer.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferRequestData {
    private String title;
    private String productTitle1;
    private String productTitle2;
    private String additionalProductCount;
    private String status;
    private LocalDateTime creationDate;
}
