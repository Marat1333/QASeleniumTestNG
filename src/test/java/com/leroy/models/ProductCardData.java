package com.leroy.models;

import ru.leroymerlin.qa.core.clients.magmobile.data.ProductItemResponse;

public class ProductCardData extends CardWidgetData {

    private String lmCode;
    private String barCode;
    private String name;
    private String price;
    private String selectedQuantity;
    private String availableQuantity;
    private String quantityType;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(String availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getQuantityType() {
        return quantityType;
    }

    public void setQuantityType(String quantityType) {
        this.quantityType = quantityType;
    }

    public String getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(String selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o==this) {
            return true;
        }else {
            return false;
        }



    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
