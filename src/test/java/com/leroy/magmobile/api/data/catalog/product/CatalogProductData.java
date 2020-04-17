package com.leroy.magmobile.api.data.catalog.product;

import com.leroy.magmobile.api.data.catalog.ProductItemData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CatalogProductData extends ProductItemData {
    private Integer topEMQuantity;
    private ExtStocks extStocks;
    private SalesPrice salesPrice;
    private Logistic logistic;
    private Double purchasePrice;
    private String purchasePriceCurrency;
    private Double primeCost;
    private String primeCostCurrency;
    private Double futurePrice;
    private String futurePriceFromDate;
    private String priceReasonOfChange;
    private Float rating;
    private Integer reviewsCount;
    private StockAreas stockAreas;
    private Inventory inventory;
}
