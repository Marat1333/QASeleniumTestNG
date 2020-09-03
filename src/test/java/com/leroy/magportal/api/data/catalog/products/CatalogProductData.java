package com.leroy.magportal.api.data.catalog.products;

import com.leroy.magportal.api.data.catalog.products.product_fields.ExtStocks;
import com.leroy.magportal.api.data.catalog.products.product_fields.Inventory;
import com.leroy.magportal.api.data.catalog.products.product_fields.PriceInfo;
import com.leroy.magportal.api.data.catalog.products.product_fields.StockAreas;
import com.leroy.magportal.api.data.catalog.suppliers.CatalogSupplierData;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CatalogProductData extends ProductData {

    private Integer topEMQuantity;
    private ExtStocks extStocks;
    private PriceInfo priceInfo;
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
    private CatalogSupplierData supplier;
    private StockAreas stocks;
}
