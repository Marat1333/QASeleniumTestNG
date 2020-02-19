package com.leroy.models;

public class SalesOrderCardData extends CardWidgetData {

    private ProductCardData productCardData;
    private Double selectedQuantity;
    private Double totalPrice;
    private Double availableTodayQuantity;

    public ProductCardData getProductCardData() {
        return productCardData;
    }

    public void setProductCardData(ProductCardData productCardData) {
        this.productCardData = productCardData;
    }

    public Double getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(Double selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getAvailableTodayQuantity() {
        return availableTodayQuantity;
    }

    public void setAvailableTodayQuantity(Double availableTodayQuantity) {
        this.availableTodayQuantity = availableTodayQuantity;
    }

    public boolean compareOnlyNotNullFields(SalesOrderCardData orderCardData) {
        if (orderCardData == null || orderCardData.getProductCardData() == null)
            return false;
        return getProductCardData().compareOnlyNotNullFields(orderCardData.getProductCardData()) &&
                equalsIfRightNotNull(selectedQuantity, orderCardData.getSelectedQuantity()) &&
                equalsIfRightNotNull(totalPrice, orderCardData.getTotalPrice()) &&
                equalsIfRightNotNull(availableTodayQuantity, orderCardData.getAvailableTodayQuantity());
    }

}
