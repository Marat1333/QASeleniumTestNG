package com.leroy.common_mashups.catalogs.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NearestShopsV2Data {

    private int shopId;
    private Double price;
    private Double altPrice;
    private String altPriceUnit;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]['Z']")
    private String dateOfChange;
    private String reasonOfChange;
    private Double recommendedPrice;
    private String recommendedUnitSale;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]['Z']")
    private LocalDateTime recommendedDateOfChange;
    private String priceUnit;
    private String priceCurrency;
    private Double distance;
    private Double availableStock;
}
