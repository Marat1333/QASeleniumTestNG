package com.leroy.magmobile.models.search;

import com.leroy.magmobile.models.CardWidgetData;
import com.leroy.umbrella_extension.magmobile.data.ProductItemData;

public class ProductCardData extends CardWidgetData {

    private String lmCode;
    private String barCode;
    private String name;
    private Double price;
    private Boolean hasAvailableStock;
    private Double availableQuantity;
    private String priceUnit;

    public ProductCardData() {
    }

    public ProductCardData(String lmCode) {
        this.lmCode = lmCode;
    }

    public String getLmCode() {
        return lmCode;
    }

    public void setLmCode(String lmCode) {
        this.lmCode = lmCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Double availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public void addAvailableQuantity(Double availableQuantity) {
        this.availableQuantity += availableQuantity;
    }

    public String getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    public Boolean isHasAvailableStock() {
        return hasAvailableStock;
    }

    public void setHasAvailableStock(Boolean hasAvailableStock) {
        this.hasAvailableStock = hasAvailableStock;
    }

    public boolean compareWithResponse(ProductItemData response) {
        if (response == null) {
            return false;
        }
        return lmCode.equals(response.getLmCode()) &&
                equalsIfLeftNotNull(barCode, response.getBarCode()) &&
                equalsIfLeftNotNull(name, response.getTitle()) &&
                equalsIfLeftNotNull(price, response.getPrice());
    }

    public boolean compareOnlyNotNullFields(ProductCardData cardData) {
        if (cardData == null)
            return false;
        return lmCode.equals(cardData.getLmCode()) &&
                equalsIfRightNotNull(barCode, cardData.getBarCode()) &&
                equalsIfRightNotNull(name, cardData.getName()) &&
                equalsIfRightNotNull(price, cardData.getPrice()) &&
                equalsIfRightNotNull(hasAvailableStock, cardData.isHasAvailableStock()) &&
                equalsIfRightNotNull(availableQuantity, cardData.getAvailableQuantity()) &&
                equalsIfRightNotNull(priceUnit, cardData.getPriceUnit());
    }

}

