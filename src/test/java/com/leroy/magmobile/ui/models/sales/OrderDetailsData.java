package com.leroy.magmobile.ui.models.sales;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.models.customer.MagLegalCustomerData;
import com.leroy.utils.RandomUtil;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;

@Data
public class OrderDetailsData {

    private SalesDocumentsConst.GiveAwayPoints deliveryType;
    private MagCustomerData customer;
    private MagLegalCustomerData orgAccount;
    private LocalDate deliveryDate;
    private String pinCode;
    private String comment;

    public OrderDetailsData() {
        this.deliveryType = SalesDocumentsConst.GiveAwayPoints.PICKUP;
    }

    public OrderDetailsData setRequiredRandomData() {
        MagCustomerData customerData = new MagCustomerData();
        customerData.setName(String.format("%s %s", RandomStringUtils.randomAlphanumeric(6),
                RandomStringUtils.randomAlphabetic(6)));
        customerData.setPhone(RandomUtil.randomPhoneNumber());
        this.customer = customerData;
        this.pinCode = RandomUtil.randomPinCode(true);
        return this;
    }

    public void assertEqualsNotNullExpectedFields(OrderDetailsData expectedOrderDetailsData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedOrderDetailsData.getDeliveryType() != null) {
            softAssert.isEquals(deliveryType, expectedOrderDetailsData.getDeliveryType(),
                    "Неверный способ получения");
        }
        if (expectedOrderDetailsData.getDeliveryDate() != null) {
            softAssert.isEquals(deliveryDate, expectedOrderDetailsData.getDeliveryDate(),
                    "Неверная дата исполнения заказа");
        }
        if (expectedOrderDetailsData.getPinCode() != null) {
            softAssert.isEquals(pinCode, expectedOrderDetailsData.getPinCode(),
                    "Неверный PIN документа");
        }
        if (expectedOrderDetailsData.getComment() != null) {
            softAssert.isEquals(comment, expectedOrderDetailsData.getComment(),
                    "Неверный комментарий документа");
        }
        if (expectedOrderDetailsData.getOrgAccount() != null) {
            orgAccount.assertEqualsNotNullExpectedFields(expectedOrderDetailsData.getOrgAccount());
        }
        if (expectedOrderDetailsData.getCustomer() != null) {
            customer.assertEqualsNotNullExpectedFields(expectedOrderDetailsData.getCustomer());
        }
        softAssert.verifyAll();
    }
}
