package com.leroy.models;

import com.leroy.umbrella_extension.magmobile.data.ProductItemResponse;

public class ProductCardData extends CardWidgetData {

    private String lmCode;
    private String barCode;
    private String name;
    private String price;
    private String selectedQuantity;
    private Boolean hasAvailableStock;
    private String availableQuantity;
    private String quantityType;

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

    public Boolean isHasAvailableStock() {
        return hasAvailableStock;
    }

    public void setHasAvailableStock(Boolean hasAvailableStock) {
        this.hasAvailableStock = hasAvailableStock;
    }

    public String getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(String selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }

    private boolean equalsIfLeftNotNull(Object arg1, Object arg2) {
        if (arg1 != null)
            return arg1.equals(arg2);
        else
            return true;
    }

    public boolean compareWithResponse(ProductItemResponse response) {
        if (response == null) {
            return false;
        }
        boolean result = lmCode.equals(response.getLmCode()) &&
                equalsIfLeftNotNull(barCode, response.getBarCode()) &&
                equalsIfLeftNotNull(name, response.getTitle());
        if (price != null) {
            try {
                result = result && Double.valueOf(price.replaceAll(",",".")).equals(Double.valueOf(response.getPrice()));
            } catch (NumberFormatException err) {
                result = false;
            }
        }
        return result;
    }
}

