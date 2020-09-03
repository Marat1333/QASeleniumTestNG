package com.leroy.magportal.ui.models.salesdoc;

import com.leroy.constants.DefectConst;
import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.utils.ParserUtil;
import lombok.Data;

@Data
public class ProductOrderCardWebData {

    private String lmCode;
    private String barCode;
    private String title;
    private Double price;
    private String priceUnit;
    private Double selectedQuantity;
    private Double totalPrice;
    private Double availableTodayQuantity;
    private Double weight; // Обший вес с учетом кол-ва

    // Discounts
    private Double totalPriceWithDiscount;
    private Double discountPercent;

    private boolean isService;

    public ProductOrderCardWebData clone() {
        ProductOrderCardWebData productOrderCardWebData = new ProductOrderCardWebData();
        productOrderCardWebData.setLmCode(lmCode);
        productOrderCardWebData.setBarCode(barCode);
        productOrderCardWebData.setTitle(title);
        productOrderCardWebData.setPriceUnit(priceUnit);
        productOrderCardWebData.setPrice(price);
        productOrderCardWebData.setSelectedQuantity(selectedQuantity);
        productOrderCardWebData.setTotalPrice(totalPrice);
        productOrderCardWebData.setAvailableTodayQuantity(availableTodayQuantity);
        productOrderCardWebData.setWeight(weight);
        productOrderCardWebData.setTotalPriceWithDiscount(totalPriceWithDiscount);
        productOrderCardWebData.setDiscountPercent(discountPercent);
        productOrderCardWebData.setService(isService);
        return productOrderCardWebData;
    }

    public void setDiscountPercent(Double discountPercent) {
        setDiscountPercent(discountPercent, false);
    }

    public void setDiscountPercent(Double discountPercent, boolean reCalculate) {
        this.discountPercent = discountPercent;
        if (reCalculate) {
            this.totalPriceWithDiscount = ParserUtil.minus(totalPrice, (totalPrice * discountPercent / 100), 2);
        }
    }

    public void assertEqualsNotNullExpectedFields(ProductOrderCardWebData expectedProduct, int iOrder, int iProduct) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedProduct.getLmCode() != null)
            softAssert.isEquals(this.getLmCode(), expectedProduct.getLmCode(),
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидался другой ЛМ код");
        if (expectedProduct.getBarCode() != null)
            softAssert.isEquals(this.getBarCode(), expectedProduct.getBarCode(),
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидался другой бар код");
        if (expectedProduct.getTitle() != null)
            softAssert.isEquals(this.getTitle(), expectedProduct.getTitle(),
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидалось другое название");
        if (expectedProduct.getPrice() != null)
            softAssert.isEquals(this.getPrice(), expectedProduct.getPrice(),
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидалась другая цена");
        if (expectedProduct.getPriceUnit() != null)
            softAssert.isEquals(this.getPriceUnit(), expectedProduct.getPriceUnit(),
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидался другой price unit");
        if (expectedProduct.getSelectedQuantity() != null)
            softAssert.isEquals(this.getSelectedQuantity(), expectedProduct.getSelectedQuantity(),
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидался другое кол-во");
        if (expectedProduct.getTotalPrice() != null)
            softAssert.isEquals(this.getTotalPrice(), expectedProduct.getTotalPrice(),
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидалась другая стоимость");
        if (expectedProduct.getTotalPriceWithDiscount() != null)
            softAssert.isEquals(this.getTotalPriceWithDiscount(), expectedProduct.getTotalPriceWithDiscount(),
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидалась другая стоимость (с учетом скидки)");
        if (expectedProduct.getDiscountPercent() != null)
            softAssert.isEquals(this.getDiscountPercent(), expectedProduct.getDiscountPercent(),
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидалась другая скидка %");
        if (expectedProduct.getAvailableTodayQuantity() != null && !DefectConst.STOCK_ISSUE)
            softAssert.isEquals(this.getAvailableTodayQuantity(), expectedProduct.getAvailableTodayQuantity(),
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидалось другое доступное кол-во");
        else if (this.getAvailableTodayQuantity() != null)
            softAssert.isTrue(this.getAvailableTodayQuantity() >= 0,
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидалось, что доступное кол-во >= 0");
        if (expectedProduct.getWeight() != null)
            softAssert.isTrue(Math.abs(this.getWeight() - expectedProduct.getWeight()) <= expectedProduct.getSelectedQuantity() * 0.011,
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидался другой вес. " +
                            "Актуальный:" + this.getWeight() + " Ожидался:" + expectedProduct.getWeight());
        else
            softAssert.isTrue(this.getWeight() > 0,
                    "Заказ #" + (iOrder + 1) + " Товар #" + (iProduct + 1) + " - ожидался вес > 0");

        softAssert.verifyAll();
    }
}
