package com.leroy.magmobile.api.data.ruptures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RuptureProductDataList {
    private List<RuptureProductData> items;
    private Integer totalCount;

    @JsonIgnore
    public void addItem(RuptureProductData productData) {
        if (items == null || totalCount == null) {
            items = new ArrayList<>();
            totalCount = 0;
        }
        items.add(productData);
        totalCount++;
    }

    @JsonIgnore
    public void removeItem(int index) {
        items.remove(index);
        totalCount--;
    }
}