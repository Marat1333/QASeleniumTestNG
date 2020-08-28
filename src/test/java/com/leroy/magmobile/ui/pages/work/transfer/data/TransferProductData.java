package com.leroy.magmobile.ui.pages.work.transfer.data;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransferProductData {
    private String lmCode;
    private String title;
    private String barCode;
    private Integer totalStock;
    private Integer orderedQuantity;

    // Для клиента в торговый зал
    private Double price;
    private Double totalPrice;
    private Integer selectedPieceQuantity;
    private Integer selectedMonoPalletQuantity;
    private Integer selectedMixPalletQuantity;

    public TransferProductData(ProductOrderCardAppData productOrderCardAppData) {
        this.lmCode = productOrderCardAppData.getLmCode();
        this.title = productOrderCardAppData.getTitle();
        this.barCode = productOrderCardAppData.getBarCode();
        this.orderedQuantity = (int) Math.round(productOrderCardAppData.getSelectedQuantity());
        this.totalStock = productOrderCardAppData.getTotalStock();
        this.price = productOrderCardAppData.getPrice();
        this.totalPrice = productOrderCardAppData.getTotalPrice();
    }

    public TransferProductData clone() {
        TransferProductData transferProductData = new TransferProductData();
        transferProductData.setLmCode(lmCode);
        transferProductData.setTitle(title);
        transferProductData.setBarCode(barCode);
        transferProductData.setTotalStock(totalStock);
        transferProductData.setOrderedQuantity(orderedQuantity);
        transferProductData.setPrice(price);
        transferProductData.setTotalPrice(totalPrice);
        transferProductData.setSelectedPieceQuantity(selectedPieceQuantity);
        transferProductData.setSelectedMonoPalletQuantity(selectedMonoPalletQuantity);
        transferProductData.setSelectedMixPalletQuantity(selectedMixPalletQuantity);
        return transferProductData;
    }

    public void setOrderedQuantity(Integer orderedQuantity, boolean recalculate) {
        this.orderedQuantity = orderedQuantity;
        this.totalPrice = this.price * orderedQuantity;
    }

    public void assertEqualsNotNullExpectedFields(TransferProductData expectedData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        softAssert.isEquals(lmCode, expectedData.getLmCode(),
                "Неверный ЛМ код");
        softAssert.isEquals(title, expectedData.getTitle(),
                "Неверное название товара");
        softAssert.isEquals(barCode, expectedData.getBarCode(),
                "Неверный бар код товара");
        if (expectedData.getTotalStock() != null) {
            softAssert.isEquals(totalStock, expectedData.getTotalStock(),
                    "Неверный запас товара на складе");
        }
        if (expectedData.getOrderedQuantity() != null) {
            softAssert.isEquals(orderedQuantity, expectedData.getOrderedQuantity(),
                    "Неверное количество товара в заявке (заказано)");
        }
        if (expectedData.getPrice() != null) {
            softAssert.isEquals(price, expectedData.getPrice(),
                    "Неверная цена товара");
        }
        if (expectedData.getTotalPrice() != null) {
            softAssert.isEquals(totalPrice, expectedData.getTotalPrice(),
                    "Неверная стоимость товара в заявке");
        }
        if (expectedData.getSelectedPieceQuantity() != null) {
            softAssert.isEquals(selectedPieceQuantity, expectedData.getSelectedPieceQuantity(),
                    "Неверный штучный запас на складе");
        }
        if (expectedData.getSelectedMonoPalletQuantity() != null) {
            softAssert.isEquals(selectedMonoPalletQuantity, expectedData.getSelectedMonoPalletQuantity(),
                    "Неверный моно-паллет запас на складе");
        }
        if (expectedData.getSelectedMixPalletQuantity() != null) {
            softAssert.isEquals(selectedMixPalletQuantity, expectedData.getSelectedMixPalletQuantity(),
                    "Неверный микс-паллет запас на складе");
        }
        softAssert.verifyAll();
    }

}

