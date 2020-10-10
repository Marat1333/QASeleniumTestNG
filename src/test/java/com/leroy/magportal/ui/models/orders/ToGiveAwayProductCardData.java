package com.leroy.magportal.ui.models.orders;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.Data;

@Data
public class ToGiveAwayProductCardData {
    private String lmCode;
    private String barCode;
    private String title;

    //private Dimension3D dimension;
    private String dimension;
    private Double price;
    private Double weight;


    private String reasonForNonGiveaway;
    private Integer createdQuantity;
    private Integer orderedQuantity;
    private Integer collectedQuantity;
    private Integer givenAwayQuantity;
    private Integer refundToClient;


    public void increaseOrderedQuantity(int val) {
        this.orderedQuantity += val;
    }

    public void decreaseOrderedQuantity(int val) {
        this.orderedQuantity -= val;
    }

    private static class Dimension3D {
        private Double length;
        private Double width;
        private Double height;
    }

    public void assertEqualsNotNullExpectedFields(int i, ToGiveAwayProductCardData expectedProductData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        softAssert.isEquals(title, expectedProductData.getTitle(),
                "Товар #" + (i + 1) + " - неверное название");
        softAssert.isEquals(dimension, expectedProductData.getDimension(),
                "Товар #" + (i + 1) + " - неверные габариты");
        softAssert.isEquals(price, expectedProductData.getPrice(),
                "Товар #" + (i + 1) + " - неверная цена");
        softAssert.isEquals(weight, expectedProductData.getWeight(),
                "Товар #" + (i + 1) + " - неверный вес");
        softAssert.isEquals(reasonForNonGiveaway, expectedProductData.reasonForNonGiveaway(),
                "Товар #" + (i + 1) + " - неверная причина отсутствия товара");
        softAssert.isEquals(orderedQuantity, expectedProductData.getOrderedQuantity(),
                "Товар #" + (i + 1) + " - неверное кол-во заказано");
        softAssert.isEquals(collectedQuantity, expectedProductData.getCollectedQuantity(),
                "Товар #" + (i + 1) + " - неверное кол-во собрано");
        softAssert.verifyAll();
    }

}
