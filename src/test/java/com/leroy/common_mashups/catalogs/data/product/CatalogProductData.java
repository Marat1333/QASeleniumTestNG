package com.leroy.common_mashups.catalogs.data.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leroy.common_mashups.catalogs.data.product.details.Characteristic;
import com.leroy.common_mashups.catalogs.data.product.details.ExtStocks;
import com.leroy.common_mashups.catalogs.data.product.details.PriceInfo;
import com.leroy.common_mashups.catalogs.data.product.details.StockAreas;
import com.leroy.common_mashups.catalogs.data.supply.CatalogSupplierData;
import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class CatalogProductData {

    @JsonProperty(required = true)
    private String lmCode;
    @JsonProperty(required = true)
    private String barCode;
    @JsonProperty(required = true)
    private String title;
    private String description;
    private String gamma;
    private String  avsDate;
    private String brand;
    private String priceCategory;
    private Boolean ctm;
    private Boolean top1000;
    private Double availableStock;
    private Boolean topEM;
    private String top;
    private String supCode;
    private List<String> images;
    private Double price;
    private String priceUnit;
    private String priceCurrency;
    private String supName;
    private Integer topEMQuantity;
    private ExtStocks extStocks;
    private PriceInfo altPrice = null;
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
    private String departmentId;
    private List<Characteristic> characteristics;
    private List<String> shops; // Вместо String должен быть класс

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
        private List<?> source;
    }
}
