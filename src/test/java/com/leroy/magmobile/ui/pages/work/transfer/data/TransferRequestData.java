package com.leroy.magmobile.ui.pages.work.transfer.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferRequestData {
    private String title;
    private String productTitle;
    private String status;
    private LocalDateTime creationDate;
}
