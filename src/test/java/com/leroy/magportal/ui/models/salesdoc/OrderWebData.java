package com.leroy.magportal.ui.models.salesdoc;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.utils.ParserUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class OrderWebData {

    private List<ProductOrderCardWebData> productCardDataList;
    private Integer productCount;
    private Double totalWeight; // кг
    private Double totalPrice; // Рубли

    public ProductOrderCardWebData getLastProduct() {
        return productCardDataList.get(productCardDataList.size() - 1);
    }

    public void removeDiscountProduct(int productIndex) {
        setDiscountPercentToProduct(productIndex, 0);
        productCardDataList.get(productIndex).setTotalPriceWithDiscount(null);
        productCardDataList.get(productIndex).setDiscountPercent(null);
    }

    public void setDiscountPercentToProduct(int productIndex, double discountPercent) {
        ProductOrderCardWebData product = productCardDataList.get(productIndex);
        Double discountAmountBefore = product.getTotalPrice() - (product.getTotalPriceWithDiscount() != null ?
                product.getTotalPriceWithDiscount() : product.getTotalPrice());
        product.setDiscountPercent(discountPercent, true);
        this.totalPrice = ParserUtil.minus(
                totalPrice + discountAmountBefore, (product.getTotalPrice() * discountPercent / 100), 2);
    }

    public void addFirstProduct(ProductOrderCardWebData product, boolean recalculateOrder) {
        List<ProductOrderCardWebData> result = new ArrayList<>();
        result.add(product);
        result.addAll(productCardDataList);
        productCardDataList = result;
        if (recalculateOrder) {
            productCount++;
            totalWeight += product.getWeight();
            totalPrice += product.getTotalPrice();
        }
    }

    public void removeProduct(int index, boolean recalculateOrder) {
        if (recalculateOrder) {
            ProductOrderCardWebData removeProduct = productCardDataList.get(index);
            totalWeight = ParserUtil.plus(totalWeight, -removeProduct.getWeight(), 2);
            totalPrice -= removeProduct.getTotalPrice();
            if (productCount != null)
                productCount--;
        }
        productCardDataList.remove(index);
    }

    public void changeProductQuantity(int productIdx, Number quantity, boolean recalculateOrder) {
        ProductOrderCardWebData productData = productCardDataList.get(productIdx);
        if (recalculateOrder) {
            double newPrice = quantity.intValue() * productData.getPrice();
            if (productData.getWeight() != null) {
                double weightOneProduct = productData.getWeight() / productData.getSelectedQuantity();
                double newWeight = weightOneProduct * quantity.intValue();
                double diffTotalWeight = ParserUtil.minus(newWeight, productData.getWeight(), 2);
                productData.setWeight(newWeight);
                totalWeight = ParserUtil.plus(totalWeight, diffTotalWeight, 2);
            }
            double diffTotalPrice = newPrice - productData.getTotalPrice();
            productData.setTotalPrice(newPrice);
            totalPrice += diffTotalPrice;
        }
        productData.setSelectedQuantity(quantity.doubleValue());
    }

    public void assertEqualsNotNullExpectedFields(OrderWebData expectedOrder, int iOrder) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedOrder.getProductCount() != null)
            softAssert.isEquals(this.getProductCount(), expectedOrder.getProductCount(),
                    "Заказ #" + (iOrder + 1) + " - ожидалась другая информация о кол-ве товаров");
        if (expectedOrder.getTotalPrice() != null)
            softAssert.isEquals(this.getTotalPrice(), expectedOrder.getTotalPrice(),
                    "Заказ #" + (iOrder + 1) + " - неверная Итого стоимость");
        if (expectedOrder.getTotalWeight() != null) {
            double productCount = expectedOrder.getProductCardDataList().stream().mapToDouble(
                    ProductOrderCardWebData::getSelectedQuantity).sum();
            softAssert.isTrue(Math.abs(this.getTotalWeight() - expectedOrder.getTotalWeight()) <= 0.05 * productCount,
                    "Заказ #" + (iOrder + 1) + " - ожидался другой вес");
        }

        softAssert.isEquals(productCardDataList.size(), expectedOrder.getProductCardDataList().size(),
                "Разное фактическое кол-во товаров в заказе");

        softAssert.verifyAll();

        for (int i = 0; i < expectedOrder.getProductCardDataList().size(); i++) {
            int iCount = i;
            Optional<ProductOrderCardWebData> expProduct = expectedOrder.getProductCardDataList().stream().filter(
                    p -> p.getLmCode().equals(productCardDataList.get(iCount).getLmCode()) &&
                            p.getSelectedQuantity().equals(productCardDataList.get(iCount).getSelectedQuantity()))
                    .findFirst();
            if (!expProduct.isPresent()) {
                expProduct = expectedOrder.getProductCardDataList().stream().filter(
                        p -> p.getLmCode().equals(productCardDataList.get(iCount).getLmCode())).findFirst();
            }
            softAssert.isTrue(expProduct.isPresent(),
                    "Заказ " + (iOrder + 1) + " - обнаружен лишний товар с ЛМ " +
                            productCardDataList.get(iCount).getLmCode());
            if (expProduct.isPresent())
                productCardDataList.get(iCount).assertEqualsNotNullExpectedFields(expProduct.get(), iOrder, i);
        }
        softAssert.verifyAll();

    }


}
