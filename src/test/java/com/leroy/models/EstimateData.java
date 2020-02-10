package com.leroy.models;

import java.util.List;

public class EstimateData {

    private List<ProductCardData> productCardDataList;
    private int countOfProducts;
    private double weight;
    private double totalPrice;

    public List<ProductCardData> getProductCardDataList() {
        return productCardDataList;
    }

    public void setProductCardDataList(List<ProductCardData> productCardDataList) {
        this.productCardDataList = productCardDataList;
    }

    public int getCountOfProducts() {
        return countOfProducts;
    }

    public void setCountOfProducts(int countOfProducts) {
        this.countOfProducts = countOfProducts;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
