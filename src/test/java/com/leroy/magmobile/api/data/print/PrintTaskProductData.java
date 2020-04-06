package com.leroy.magmobile.api.data.print;

import lombok.Data;

@Data
public class PrintTaskProductData {

    public PrintTaskProductData(){

    }

    public PrintTaskProductData(PrintTaskProductData data){
        this.setLmCode(data.lmCode);
        this.setBarCode(data.getBarCode());
        this.setSize(data.getSize());
        this.setQuantity(data.getQuantity());
        this.setPrice(data.getPrice());
        this.setPriceUnit(data.getPriceUnit());
        this.setTitle(data.getTitle());
        this.setSalesPrice(data.getSalesPrice());
        this.setFuturePriceFromDate(data.getFuturePriceFromDate());
        this.setPriceReasonOfChange(data.getPriceReasonOfChange());
        this.setRecommendedPrice(data.getRecommendedPrice());
        this.setPriceCurrency(data.getPriceCurrency());
    }

    private String lmCode;
    private String barCode;
    private String size;
    private int quantity;
    private Double price;
    private String priceUnit;
    private String title;
    private Double salesPrice;
    private String futurePriceFromDate;
    private String priceReasonOfChange;
    private Double recommendedPrice;
    private String priceCurrency;
}
