package com.leroy.magmobile.api.helpers;

import com.leroy.magmobile.api.ApiClientProvider;
import com.leroy.magmobile.api.clients.CustomerClient;
import com.leroy.magmobile.api.data.customer.CustomerData;
import com.leroy.magmobile.api.data.customer.CustomerListData;
import com.leroy.magmobile.api.data.customer.CustomerSearchFilters;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Optional;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class CustomerHelper extends ApiClientProvider {

    @Step("Search for customer id by phone = {phone}")
    public String getFirstCustomerIdByPhone(String phone) {
        CustomerClient customerClient = getCustomerClient();
        CustomerSearchFilters customerSearchFilters = new CustomerSearchFilters();
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);
        customerSearchFilters.setDiscriminantType(CustomerSearchFilters.DiscriminantType.PHONENUMBER);
        customerSearchFilters.setDiscriminantValue(phone);

        Response<CustomerListData> response = customerClient.searchForCustomers(customerSearchFilters);
        assertThat(response, successful());
        Optional<CustomerData> customerData = response.asJson().getItems().stream().findFirst();
        assertThat("Couldn't find any customer with phone = " + phone, customerData.isPresent());
        return customerData.orElse(new CustomerData()).getCustomerNumber();
    }

}
