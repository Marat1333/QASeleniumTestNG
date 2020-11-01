package com.leroy.common_mashups.helpers;

import com.google.inject.Inject;
import com.leroy.common_mashups.clients.CustomerClient;
import com.leroy.common_mashups.data.customer.CustomerData;
import com.leroy.common_mashups.data.customer.CustomerListData;
import com.leroy.common_mashups.data.customer.CustomerResponseBodyData;
import com.leroy.common_mashups.data.customer.CustomerSearchFilters;
import com.leroy.magportal.api.helpers.BaseHelper;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Optional;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class CustomerHelper extends BaseHelper {

    @Inject
    CustomerClient customerClient;

    @Step("Search for customer id by phone = {phone}")
    public String getFirstCustomerIdByPhone(String phone) {
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

    @Step("Create new customer")
    public CustomerData createCustomer() {
        CustomerData customerData = new CustomerData();
        customerData.generateRandomValidRequiredData(true, true);
        Response<CustomerResponseBodyData> resp = customerClient.createCustomer(customerData);
        customerData = customerClient.assertThatIsCreatedAndGetData(resp, customerData);
        return customerData;
    }

}
