package com.leroy.magmobile.api.data.catalog.product;

import com.leroy.magmobile.api.data.catalog.ProductItemData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CatalogProductData extends ProductItemData {
    private Integer topEMQuantity;
    private ExtStocks extStocks;
    private PriceInfo priceInfo;
    private Logistic logistic;
    private PriceInfo purchasePrice;
    private String purchasePriceCurrency;
    private Double primeCost;
    private String primeCostCurrency;
    private PriceInfo futurePrice;
    private String futurePriceFromDate;
    private String priceReasonOfChange;
    private Float rating;
    private Integer reviewsCount;
    private StockAreas stockAreas;
    private Inventory inventory;
    private PriceInfo recommendedPrice;
    private String groupId;
    private String classId;
    private String subclassId;
    private List<String> barCodes;
}
