package com.leroy.magmobile.ui.models.sales;

import com.leroy.magmobile.ui.models.MagCustomerData;
import com.leroy.utils.RandomUtil;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;

@Data
public class OrderDetailsData {

    public enum DeliveryType {
        PICKUP("Самовывоз"), DELIVERY("Доставка");

        String value;

        DeliveryType(String val) {
            this.value = val;
        }

        public String getValue() {
            return value;
        }
    }

    private DeliveryType deliveryType;
    private MagCustomerData customer;
    private LocalDate deliveryDate;
    private String pinCode;
    private String comment;

    public OrderDetailsData setRequiredRandomData() {
        MagCustomerData customerData = new MagCustomerData();
        customerData.setName(String.format("%s %s", RandomStringUtils.randomAlphanumeric(6),
                RandomStringUtils.randomAlphabetic(6)));
        customerData.setPhone(RandomUtil.randomPhoneNumber());
        this.customer = customerData;
        this.pinCode = RandomUtil.randomPinCode(true);
        return this;
    }
}
