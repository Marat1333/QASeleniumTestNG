package com.leroy.umbrella_extension.magmobile.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductItemResponse extends ResponseItem{

    private String lmCode;
    private String barCode;
    private String title;
    private String gamma;
    private LocalDateTime avsDate;
    private String priceCategory;
    private Boolean ctm;
    private Boolean top1000;
    private Integer availableStock;
    private Boolean topEM;
    private Integer top;
    private String supCode;
    private List<String> images;
    private Double price;
    private String altPrice;
    private String priceUnit;
    private String priceCurrency;
    private String supName;

}
