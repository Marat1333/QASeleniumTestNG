package com.leroy.umbrella_extension.magmobile.data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class ServiceItemListResponse extends ResponseList{

    private List<ServiceItemResponse> items;
    private Integer totalCount;

    @Override
    public List<ServiceItemResponse> getItems() {
        return items;
    }

    public Integer getTotalCount() {
        return totalCount;
    }
}
