package com.leroy.magportal.ui.models.orders;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.Data;

@Data
public class ToGiveAwayProductCardData {
    private String lmCode;
    private String barCode;
    private String title;


    private String reasonForNonGiveaway;
    private Integer createdQuantity;
    private Integer orderedQuantity;
    private Integer collectedQuantity;
    private Integer givenAwayQuantity;
    private Integer refundToClient;
    private Integer toGiveAwayQuantity;


    public void increaseToGiveAwayQuantity(int val) { this.toGiveAwayQuantity += val;
    }

    public void decreaseToGiveAwayQuantity(int val) {
        this.toGiveAwayQuantity -= val;
    }



    public void assertEqualsNotNullExpectedFields(int i, ToGiveAwayProductCardData expectedProductData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        softAssert.isEquals(title, expectedProductData.getTitle(),
                "Товар #" + (i + 1) + " - неверное название");
        softAssert.isEquals(reasonForNonGiveaway, expectedProductData.getReasonForNonGiveaway(),
                "Товар #" + (i + 1) + " - неверная причина невыдачи товара");
        softAssert.isEquals(orderedQuantity, expectedProductData.getOrderedQuantity(),
                "Товар #" + (i + 1) + " - неверное кол-во заказано");
        softAssert.isEquals(collectedQuantity, expectedProductData.getCollectedQuantity(),
                "Товар #" + (i + 1) + " - неверное кол-во собрано");
        softAssert.isEquals(givenAwayQuantity, expectedProductData.getGivenAwayQuantity(),
                "Товар #" + (i + 1) + " - неверное кол-во выдано");
        softAssert.verifyAll();
    }

}
