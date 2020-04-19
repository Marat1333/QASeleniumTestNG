package com.leroy.magmobile.api.data.kladr;

import lombok.Data;

import java.util.List;

@Data
public class KladrItemDataList {
    private Integer totalCount;
    private List<KladrItemData> items;
}
