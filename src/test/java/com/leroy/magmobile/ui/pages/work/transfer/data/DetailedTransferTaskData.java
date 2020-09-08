package com.leroy.magmobile.ui.pages.work.transfer.data;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.pages.work.transfer.enums.TransferTaskTypes;
import com.leroy.utils.ParserUtil;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
public class DetailedTransferTaskData {

    private String number;
    private TransferTaskTypes pickupPlace;
    private MagCustomerData client;
    private LocalDate deliveryDate;
    private LocalTime deliveryTime;
    private List<TransferProductData> products;
    private Double totalPrice;

    public DetailedTransferTaskData clone() {
        DetailedTransferTaskData detailedTransferTaskData = new DetailedTransferTaskData();
        detailedTransferTaskData.setNumber(number);
        detailedTransferTaskData.setPickupPlace(pickupPlace);
        detailedTransferTaskData.setClient(client);
        detailedTransferTaskData.setDeliveryDate(deliveryDate);
        detailedTransferTaskData.setDeliveryTime(deliveryTime);
        List<TransferProductData> cloneProducts = new ArrayList<>();
        for (TransferProductData transferProductData : products) {
            cloneProducts.add(transferProductData.clone());
        }
        detailedTransferTaskData.setProducts(cloneProducts);
        detailedTransferTaskData.setTotalPrice(totalPrice);
        return detailedTransferTaskData;
    }

    public void assertEqualsNotNullExpectedFields(DetailedTransferTaskData expectedData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedData.getNumber() != null) {
            softAssert.isEquals(number, expectedData.getNumber(),
                    "Неверный номер заявки");
        }
        if (expectedData.getPickupPlace() != null) {
            softAssert.isEquals(pickupPlace, expectedData.getPickupPlace(),
                    "Неверное место выдачи товара");
        }
        if (expectedData.getClient() != null) {
            client.assertEqualsNotNullExpectedFields(expectedData.getClient());
        }
        if (expectedData.getDeliveryDate() != null) {
            softAssert.isEquals(deliveryDate, expectedData.getDeliveryDate(),
                    "Неверное дата поставки товара");
        }
        if (expectedData.getDeliveryTime() != null) {
            long diffTime = ChronoUnit.MINUTES.between(deliveryTime, expectedData.getDeliveryTime());
            softAssert.isTrue(Math.abs(diffTime) <= 1,
                    "Неверное время поставки товара. Актуальное: " + deliveryTime + " Ожидалось: " + expectedData.getDeliveryTime());
        }
        if (expectedData.getProducts() != null) {
            softAssert.isEquals(expectedData.getProducts().size(), products.size(),
                    "Разное кол-во товаров в заявке");
            softAssert.verifyAll();
            for (int i = 0; i < expectedData.getProducts().size(); i++) {
                products.get(i).assertEqualsNotNullExpectedFields(expectedData.getProducts().get(i));
            }
        }
        if (expectedData.getTotalPrice() != null) {
            softAssert.isEquals(totalPrice, expectedData.getTotalPrice(),
                    "Неверная общая стоимость");
        }

        softAssert.verifyAll();
    }

    public void removeProduct(int index) {
        if (totalPrice != null)
            totalPrice -= products.get(index).getTotalPrice();
        products.remove(index);
    }

    public void changeProductQuantity(int index, int value) {
        TransferProductData productData = products.get(index);
        double price = productData.getPrice();
        double quantityDiff = value - productData.getOrderedQuantity();
        productData.setOrderedQuantity(value, true);
        totalPrice += ParserUtil.multiply(price, quantityDiff, 2);
    }

}
