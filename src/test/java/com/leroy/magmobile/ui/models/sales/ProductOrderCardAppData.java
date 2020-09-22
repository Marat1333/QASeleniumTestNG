package com.leroy.magmobile.ui.models.sales;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.utils.ParserUtil;
import lombok.Data;

/**
 * Карточка товара внутри заказа
 * Экраны: Корзина, Смета и т.д.
 */
@Data
public class ProductOrderCardAppData {

    private String lmCode;
    private String barCode;
    private String title;
    private Double price;
    private String priceUnit;

    private Double selectedQuantity;
    private Double totalPrice;
    private Double totalPriceWithDiscount;
    private Double discountPercent;

    private Integer availableTodayQuantity;
    private Boolean avs;
    private Boolean topEm;
    private Boolean hasAvailableQuantity;

    // Stock
    private Integer totalStock;
    private Integer pieceQuantityInStock;
    private Integer monoPalletQuantityInStock;
    private Integer mixPalletQuantityInStock;

    public void setDiscountPercent(Double discountPercent) {
        setDiscountPercent(discountPercent, false);
    }

    public void setDiscountPercent(Double discountPercent, boolean reCalculate) {
        this.discountPercent = discountPercent;
        if (reCalculate) {
            this.totalPriceWithDiscount = ParserUtil.minus(totalPrice, (totalPrice * discountPercent / 100), 2);
        }
    }

    public void assertEqualsNotNullExpectedFields(int index, ProductOrderCardAppData expectedProductCardData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        softAssert.isEquals(lmCode, expectedProductCardData.getLmCode(),
                "Товар " + (index + 1) + " - неверный ЛМ код");
        softAssert.isEquals(title, expectedProductCardData.getTitle(),
                "Товар " + (index + 1) + " - неверное название товара");
        softAssert.isEquals(price, expectedProductCardData.getPrice(),
                "Товар " + (index + 1) + " - неверная цена товара");
        softAssert.isEquals(priceUnit, expectedProductCardData.getPriceUnit(),
                "Товар " + (index + 1) + " - неверный price unit товара");
        softAssert.isEquals(selectedQuantity, expectedProductCardData.getSelectedQuantity(),
                "Товар " + (index + 1) + " - неверное выбранное кол-во товара");
        softAssert.isEquals(totalPrice, expectedProductCardData.getTotalPrice(),
                "Товар " + (index + 1) + " - неверная сумма товара");
        if (expectedProductCardData.getTotalPriceWithDiscount() != null &&
                !(expectedProductCardData.getTotalPriceWithDiscount()
                        .equals(expectedProductCardData.getTotalPrice()) &&
                        totalPriceWithDiscount == null)) {
            softAssert.isEquals(totalPriceWithDiscount, expectedProductCardData.getTotalPriceWithDiscount(),
                    "Товар " + (index + 1) + " - неверная сумма (с учетом скидки) товара");
        }
        if (expectedProductCardData.getAvailableTodayQuantity() != null) {
            softAssert.isEquals(availableTodayQuantity, expectedProductCardData.getAvailableTodayQuantity(),
                    "Товар " + (index + 1) + " - неверное доступное кол-во товара");
        }
        if (expectedProductCardData.getHasAvailableQuantity() != null) {
            if (expectedProductCardData.getHasAvailableQuantity())
                softAssert.isTrue(availableTodayQuantity > 0,
                        "Товар " + (index + 1) + " - ожидалось, что доступное кол-во товара больше нуля");
            else
                softAssert.isTrue(availableTodayQuantity <= 0,
                        "Товар " + (index + 1) + " - ожидалось, что доступное кол-во товара ноль или меньше");
        }
        if (expectedProductCardData.getAvs() != null) {
            softAssert.isEquals(avs, expectedProductCardData.getAvs(),
                    "Товар " + (index + 1) + " - должен иметь признак AVS");
        }
        if (expectedProductCardData.getTopEm() != null) {
            softAssert.isEquals(topEm, expectedProductCardData.getTopEm(),
                    "Товар " + (index + 1) + " - должен иметь признак TOP EM");
        }
        if (!((expectedProductCardData.getDiscountPercent() == null || expectedProductCardData.getDiscountPercent() == 0.0) &&
                discountPercent == null)) {
            softAssert.isEquals(discountPercent, expectedProductCardData.getDiscountPercent(),
                    "Товар " + (index + 1) + " - неверная скидка % товара");
        }
        if (expectedProductCardData.getTotalStock() != null) {
            softAssert.isEquals(totalStock, expectedProductCardData.getTotalStock(),
                    "Товар " + (index + 1) + " - неверный запас на складе");
        }
        if (expectedProductCardData.getPieceQuantityInStock() != null) {
            softAssert.isEquals(pieceQuantityInStock, expectedProductCardData.getPieceQuantityInStock(),
                    "Товар " + (index + 1) + " - неверный штучный запас на складе");
        }
        if (expectedProductCardData.getMonoPalletQuantityInStock() != null) {
            softAssert.isEquals(monoPalletQuantityInStock, expectedProductCardData.getMonoPalletQuantityInStock(),
                    "Товар " + (index + 1) + " - неверный моно-паллет запас на складе");
        }
        if (expectedProductCardData.getMixPalletQuantityInStock() != null) {
            softAssert.isEquals(mixPalletQuantityInStock, expectedProductCardData.getMixPalletQuantityInStock(),
                    "Товар " + (index + 1) + " - неверный микс-паллет запас на складе");
        }
        softAssert.verifyAll();
    }

    public void assertEqualsNotNullExpectedFields(ProductOrderCardAppData orderCardData) {
        assertEqualsNotNullExpectedFields(0, orderCardData);
    }

    public ProductOrderCardAppData copy() {
        ProductOrderCardAppData productData = new ProductOrderCardAppData();
        productData.setLmCode(lmCode);
        productData.setBarCode(barCode);
        productData.setTitle(title);
        productData.setPrice(price);
        productData.setPriceUnit(priceUnit);
        productData.setSelectedQuantity(selectedQuantity);
        productData.setTotalPrice(totalPrice);
        productData.setTotalPriceWithDiscount(totalPriceWithDiscount);
        productData.setAvailableTodayQuantity(availableTodayQuantity);
        productData.setDiscountPercent(discountPercent);
        productData.setAvs(avs);
        productData.setTopEm(topEm);
        productData.setHasAvailableQuantity(hasAvailableQuantity);
        productData.setTotalStock(totalStock);
        productData.setPieceQuantityInStock(pieceQuantityInStock);
        productData.setMonoPalletQuantityInStock(monoPalletQuantityInStock);
        productData.setMixPalletQuantityInStock(mixPalletQuantityInStock);
        return productData;
    }

}
