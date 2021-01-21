package com.leroy.common_mashups.catalogs.data.product;

import com.leroy.common_mashups.catalogs.data.product.details.ExtStocks;
import com.leroy.common_mashups.catalogs.data.product.details.PriceInfo;
import com.leroy.common_mashups.catalogs.data.product.details.StockAreas;
import com.leroy.common_mashups.catalogs.data.supply.CatalogSupplierData;
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
    private PriceInfo salesPrice;
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
    private CatalogSupplierData supplier;
    private StockAreas stocks;

    @Data
    private static class Logistic {
        private Float width;
        private Float height;
        private Float depth;
        private Float weight;
    }

    @Data
    private static class Inventory {
        private Integer totalQuantity;
        private List<String> source;
    }
}
