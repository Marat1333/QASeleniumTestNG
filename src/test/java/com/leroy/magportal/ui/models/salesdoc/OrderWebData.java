package com.leroy.magportal.ui.models.salesdoc;

import com.leroy.utils.ParserUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderWebData {

    private List<ProductOrderCardWebData> productCardDataList;
    private Integer productCount;
    private Double totalWeight; // кг
    private Double totalPrice; // Рубли

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
            totalWeight = ParserUtil.sum(totalWeight, - removeProduct.getWeight(), 2);
            totalPrice -= removeProduct.getTotalPrice();
            productCount--;
        }
        productCardDataList.remove(index);
    }

    public void changeProductQuantity(int productIdx, Number quantity, boolean recalculateOrder) {
        ProductOrderCardWebData productData = productCardDataList.get(productIdx);
        double weightOneProduct = productData.getWeight() / productData.getSelectedQuantity();
        productData.setSelectedQuantity(quantity.doubleValue());
        if (recalculateOrder) {
            double newPrice = quantity.intValue() * productData.getPrice();
            double newWeight = weightOneProduct * quantity.intValue();
            double diffTotalPrice = newPrice - productData.getTotalPrice();
            double diffTotalWeight = newWeight - productData.getWeight();
            productData.setTotalPrice(newPrice);
            productData.setWeight(newWeight);
            totalPrice+=diffTotalPrice;
            totalWeight+=diffTotalWeight;
        }
    }

}
