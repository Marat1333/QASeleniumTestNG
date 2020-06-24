package com.leroy.magportal.api.data.catalog.products;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.leroy.magmobile.api.data.catalog.Characteristic;
import com.leroy.magportal.api.data.catalog.products.product_fields.PriceInfo;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProductData {

    @JsonProperty(required = true)
    private String lmCode;
    @JsonProperty(required = true)
    private String barCode;
    @JsonProperty(required = true)
    private String title;
    private String description;
    private String gamma;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date avsDate;
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
    private PriceInfo altPrice;
    private String altPriceUnit;
    private String priceUnit;
    private String priceCurrency;
    private String supName;

    // SalesDoc Products содержат также такие поля: (Или это другая сущность?)
    private String departmentId;
    private List<Characteristic> characteristics;
    private List<String> shops; // Вместо String должен быть класс

}