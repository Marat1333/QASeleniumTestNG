package com.leroy.magmobile.api.data.sales;

import lombok.Data;

import java.util.List;

@Data
public class SalesDocumentListResponse {
    private Integer totalCount;
    List<SalesDocumentResponseData> salesDocuments;
}
