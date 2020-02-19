package com.leroy.models;

import java.util.List;

public class SalesOrderData extends CardWidgetData {

    private List<SalesOrderCardData> orderCardDataList;
    private Double productCount;
    private Double totalWeight; // кг
    private Double totalPrice; // Рубли

    public List<SalesOrderCardData> getOrderCardDataList() {
        return orderCardDataList;
    }

    public void setOrderCardDataList(List<SalesOrderCardData> orderCardDataList) {
        this.orderCardDataList = orderCardDataList;
    }

    public Double getProductCount() {
        return productCount;
    }

    public void setProductCount(Double productCount) {
        this.productCount = productCount;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    /*public boolean compareOnlyNotNullFields(OrderData orderData) {
        if (orderData == null || orderData.getProductCardData() == null)
            return false;
        return getProductCardData().compareOnlyNotNullFields(orderData.getProductCardData()) &&
                equalsIfRightNotNull(selectedQuantity, orderData.getSelectedQuantity()) &&
                equalsIfRightNotNull(totalPrice, orderData.getTotalPrice()) &&
                equalsIfRightNotNull(availableTodayQuantity, orderData.getAvailableTodayQuantity());
    }*/
}
