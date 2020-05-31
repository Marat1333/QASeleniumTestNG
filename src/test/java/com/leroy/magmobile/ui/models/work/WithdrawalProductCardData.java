package com.leroy.magmobile.ui.models.work;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.Data;

@Data
public class WithdrawalProductCardData {

    private String lmCode;
    private Double selectedQuantity;
    private String title;
    private String priceUnit;
    private Double availableQuantity;

    public void assertEqualsNotNullExpectedFields(int index, WithdrawalProductCardData expectedProductCardData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        softAssert.isEquals(lmCode, expectedProductCardData.getLmCode(),
                "Товар " + (index + 1) + " - неверный ЛМ код");
        softAssert.isEquals(title, expectedProductCardData.getTitle(),
                "Товар " + (index + 1) + " - неверное название товара");
        softAssert.isEquals(priceUnit, expectedProductCardData.getPriceUnit(),
                "Товар " + (index + 1) + " - неверный price unit товара");
        softAssert.isEquals(selectedQuantity, expectedProductCardData.getSelectedQuantity(),
                "Товар " + (index + 1) + " - неверное выбранное кол-во товара");
        softAssert.isEquals(availableQuantity, expectedProductCardData.getAvailableQuantity(),
                "Товар " + (index + 1) + " - неверное доступное кол-во товара");
        softAssert.verifyAll();
    }

    public void plusAvailableQuantity(Double q) {
        this.availableQuantity += q;
    }

    public void minusAvailableQuantity(Double q) {
        this.availableQuantity -= q;
    }
}
