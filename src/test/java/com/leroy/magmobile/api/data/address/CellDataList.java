package com.leroy.magmobile.api.data.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class CellDataList {
    private Integer standId;
    private List<CellData> items;

    @JsonIgnore
    public void addItem(CellData itemData) {
        items.add(itemData);
    }

    @JsonIgnore
    public void updateLastItem(CellData itemData) {
        items.remove(items.size() - 1);
        items.add(itemData);
    }
}
