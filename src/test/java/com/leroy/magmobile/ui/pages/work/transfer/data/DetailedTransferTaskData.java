package com.leroy.magmobile.ui.pages.work.transfer.data;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.pages.work.transfer.modal.SelectPickupPointModal;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DetailedTransferTaskData {

    private SelectPickupPointModal.Options pickupPlace;
    private MagCustomerData client;
    private LocalDate deliveryDate;
    private LocalTime deliveryTime;
    private List<TransferProductData> products;

    public DetailedTransferTaskData clone() {
        DetailedTransferTaskData detailedTransferTaskData = new DetailedTransferTaskData();
        detailedTransferTaskData.setPickupPlace(pickupPlace);
        detailedTransferTaskData.setClient(client);
        detailedTransferTaskData.setDeliveryDate(deliveryDate);
        detailedTransferTaskData.setDeliveryTime(deliveryTime);
        List<TransferProductData> cloneProducts = new ArrayList<>();
        for (TransferProductData transferProductData : products) {
            cloneProducts.add(transferProductData.clone());
        }
        detailedTransferTaskData.setProducts(cloneProducts);
        return detailedTransferTaskData;
    }

    public void assertEqualsNotNullExpectedFields(DetailedTransferTaskData expectedData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
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
            softAssert.isEquals(deliveryTime, expectedData.getDeliveryTime(),
                    "Неверное ожидаемое время поставки товара");
        }
        if (expectedData.getProducts()!=null) {
            softAssert.isEquals(expectedData.getProducts().size(), products.size(),
                    "Разное кол-во товаров в заявке");
            softAssert.verifyAll();
            for (int i=0; i < expectedData.getProducts().size(); i++) {
                products.get(i).assertEqualsNotNullExpectedFields(expectedData.getProducts().get(i));
            }
        }

        softAssert.verifyAll();
    }

}
