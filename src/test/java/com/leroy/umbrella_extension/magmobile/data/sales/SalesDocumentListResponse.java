package com.leroy.umbrella_extension.magmobile.data.sales;

import lombok.Data;

import java.util.List;

@Data
public class SalesDocumentListResponse {
    private Integer totalCount;
    List<SalesDocumentResponse> salesDocuments;
}
