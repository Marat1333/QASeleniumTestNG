package com.leroy.magportal.ui.models.picking;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.Data;

@Data
public class PickingProductCardData {
    private String lmCode;
    private String barCode;
    private String title;
    private Integer department;
    //private Dimension3D dimension;
    private String dimension;
    private Double price;
    private Double weight;

    private String reasonOfLack;
    private Double  stockQuantity;
    private Double  orderedQuantity;
    private Double  collectedQuantity;

    public PickingProductCardData clone() {
        PickingProductCardData pickingProductCardData = new PickingProductCardData();
        pickingProductCardData.setLmCode(lmCode);
        pickingProductCardData.setBarCode(barCode);
        pickingProductCardData.setTitle(title);
        pickingProductCardData.setDepartment(department);
        pickingProductCardData.setDimension(dimension);
        pickingProductCardData.setPrice(price);
        pickingProductCardData.setWeight(weight);
        pickingProductCardData.setReasonOfLack(reasonOfLack);
        pickingProductCardData.setStockQuantity(stockQuantity);
        pickingProductCardData.setOrderedQuantity(orderedQuantity);
        pickingProductCardData.setCollectedQuantity(collectedQuantity);
        return pickingProductCardData;
    }

    public void increaseOrderedQuantity(double val) {
        this.orderedQuantity += val;
    }

    public void decreaseOrderedQuantity(double val) {
        this.orderedQuantity -= val;
    }

    private static class Dimension3D {
        private Double length;
        private Double width;
        private Double height;
    }

    public void assertEqualsNotNullExpectedFields(int i, PickingProductCardData expectedProductData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        softAssert.isEquals(lmCode, expectedProductData.getLmCode(),
                "Товар #" + (i + 1) + " - неверный ЛМ код");
        softAssert.isEquals(barCode, expectedProductData.getBarCode(),
                "Товар #" + (i + 1) + " - неверный бар код");
        softAssert.isEquals(title, expectedProductData.getTitle(),
                "Товар #" + (i + 1) + " - неверное название");
        softAssert.isEquals(department, expectedProductData.getDepartment(),
                "Товар #" + (i + 1) + " - неверный отдел");
        softAssert.isEquals(dimension, expectedProductData.getDimension(),
                "Товар #" + (i + 1) + " - неверные габариты");
        softAssert.isEquals(price, expectedProductData.getPrice(),
                "Товар #" + (i + 1) + " - неверная цена");
        softAssert.isEquals(weight, expectedProductData.getWeight(),
                "Товар #" + (i + 1) + " - неверный вес");
        softAssert.isEquals(reasonOfLack, expectedProductData.getReasonOfLack(),
                "Товар #" + (i + 1) + " - неверная причина отсутствия товара");
        softAssert.isEquals(stockQuantity, expectedProductData.getStockQuantity(),
                "Товар #" + (i + 1) + " - неверное кол-во на складе");
        softAssert.isEquals(orderedQuantity, expectedProductData.getOrderedQuantity(),
                "Товар #" + (i + 1) + " - неверное кол-во заказано");
        softAssert.isEquals(collectedQuantity, expectedProductData.getCollectedQuantity(),
                "Товар #" + (i + 1) + " - неверное кол-во собрано");
        softAssert.verifyAll();
    }

}
