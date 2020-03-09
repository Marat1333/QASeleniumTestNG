package com.leroy.umbrella_extension.magmobile.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.leroy.umbrella_extension.magmobile.data.catalog.Characteristic;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductItemData {

    @JsonProperty(required = true)
    private String lmCode;
    @JsonProperty(required = true)
    private String barCode;
    @JsonProperty(required = true)
    private String title;
    private String description;
    private String gamma;
    private LocalDateTime avsDate;
    private String brand;
    private String priceCategory;
    private Boolean ctm;
    private Boolean top1000;
    private Float availableStock;
    private Boolean topEM;
    private Integer top;
    private String supCode;
    private List<String> images;
    private Double price;
    private String altPrice;
    private String altPriceUnit;
    private String priceUnit;
    private String priceCurrency;
    private String supName;

    // SalesDoc Products содержат также такие поля: (Или это другая сущность?)
    private String departmentId;
    private List<Characteristic> characteristics;
    private List<String> shops; // Вместо String должен быть класс

}
