package com.leroy.umbrella_extension.magmobile.data.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceItemDataList {

    private List<ServiceItemData> items;
    private Integer totalCount;
}
