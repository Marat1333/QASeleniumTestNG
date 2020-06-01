package com.leroy.magmobile.api.data.catalog.product;

import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magportal.api.data.ProductData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CatalogProductData extends ProductData {
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
    private Float rating;
    private Integer reviewsCount;
    private StockAreas stockAreas;
    private Inventory inventory;
    private PriceInfo recommendedPrice;
    private String groupId;
    private String classId;
    private String subclassId;
    private List<String> barCodes;
    private String itemType;
    private PriceInfo salesPrice;
}
