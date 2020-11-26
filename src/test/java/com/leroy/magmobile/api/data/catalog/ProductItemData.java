package com.leroy.magmobile.api.data.catalog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class ProductItemData {

    @JsonProperty(required = true)
    private String lmCode;
    @JsonProperty(required = true)
    private String barCode;
    @JsonProperty(required = true)
    private String title;
    private String description;
    private String gamma;
    private String avsDate;
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
    private Double altPrice;
    private String altPriceUnit;
    private String priceUnit;
    private String priceCurrency;
    private String supName;

    // SalesDoc Products содержат также такие поля: (Или это другая сущность?)
    private String departmentId;
    private List<Characteristic> characteristics;
    private List<String> shops; // Вместо String должен быть класс

    @JsonIgnore
    public ZonedDateTime getAvsDateAsZonedDateTime() {
        return ZonedDateTime.parse(avsDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]['Z'][XXX]"));
    }

}
