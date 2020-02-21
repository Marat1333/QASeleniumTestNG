package com.leroy.umbrella_extension.magmobile.data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class ProductItemListResponse extends ResponseList{

    private List<ProductItemResponse> items;

    @Override
    public List<ProductItemResponse> getItems() {
        return items;
    }
}
