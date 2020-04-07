package com.leroy.magmobile.api.data.address;

import lombok.Data;

import java.util.List;

@Data
public class CellData {
    private Integer standId;
    private List<CellItemData> items;

    public void addItem(CellItemData itemData) {
        items.add(itemData);
    }

    public void updateLastItem(CellItemData itemData) {
        items.remove(items.size()-1);
        items.add(itemData);
    }
}
