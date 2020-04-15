package com.leroy.magmobile.api.data.ruptures;

import lombok.Data;

import java.util.List;

@Data
public class ResRuptureSessionDataList {
    private List<ResRuptureSessionData> items;
    private Integer totalCount;
}
