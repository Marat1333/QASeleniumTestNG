package com.leroy.magmobile.api.data.ruptures;

import lombok.Data;

@Data
public class ResRuptureSessionData {

    private Integer storeId;
    private String finishedOn;
    private String createdOn;
    private String createdByLdap;
    private Integer completedProductCount;
    private Integer totalProductCount;
    private String userFullName;
    private Integer sessionId;
    private String status;
    private String type;

}
