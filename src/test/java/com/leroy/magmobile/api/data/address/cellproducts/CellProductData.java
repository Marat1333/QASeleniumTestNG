package com.leroy.magmobile.api.data.address.cellproducts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leroy.magmobile.api.data.address.CellData;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CellProductData extends ProductItemData {

    private Integer quantity;
    private List<ProductCellData> lsAddressCells;

    @JsonIgnore
    public void setAddressCells(CellData... cells) {
        lsAddressCells = new ArrayList<>();
        for (CellData cell : cells) {
            ProductCellData productCellData = new ProductCellData();
            productCellData.setId(cell.getId());
            productCellData.setCode(cell.getCode());
            productCellData.setPosition(cell.getPosition());
            productCellData.setQuantity(cell.getProductsCount());
            productCellData.setShelf(cell.getShelf());
            productCellData.setStandId(cell.getStandId());
            productCellData.setType(cell.getType());
            lsAddressCells.add(productCellData);
        }
    }

}
