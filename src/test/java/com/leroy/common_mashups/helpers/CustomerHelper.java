package com.leroy.common_mashups.helpers;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import com.google.inject.Inject;
import com.leroy.common_mashups.customer_accounts.clients.CustomerClient;
import com.leroy.common_mashups.customer_accounts.data.CustomerData;
import com.leroy.common_mashups.customer_accounts.data.CustomerListData;
import com.leroy.common_mashups.customer_accounts.data.CustomerResponseBodyData;
import com.leroy.common_mashups.customer_accounts.data.CustomerSearchFilters;
import com.leroy.core.configuration.Log;
import com.leroy.magportal.api.helpers.BaseHelper;
import io.qameta.allure.Step;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.Assert;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CustomerHelper extends BaseHelper {

    @Inject
    CustomerClient customerClient;

    @Step("Search for customer id by phone = {phone}")
    public String getFirstCustomerIdByPhone(String phone) {
        CustomerSearchFilters customerSearchFilters = new CustomerSearchFilters();
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);
        customerSearchFilters
                .setDiscriminantType(CustomerSearchFilters.DiscriminantType.PHONENUMBER);
        customerSearchFilters.setDiscriminantValue(phone);

        Response<CustomerListData> response = customerClient
                .searchForCustomers(customerSearchFilters);
        assertThat(response, successful());
        Optional<CustomerData> customerData = response.asJson().getItems().stream().findFirst();
        assertThat("Couldn't find any customer with phone = " + phone, customerData.isPresent());
        return customerData.orElse(new CustomerData()).getCustomerNumber();
    }

    @Step("Create new customer")
    public CustomerData createCustomer(CustomerData customerData) {
        Response<CustomerResponseBodyData> resp = customerClient.createCustomer(customerData);
        customerData = customerClient.assertThatIsCreatedAndGetData(resp, customerData);
        return customerData;
    }

    @Step("Create new customer")
    public CustomerData createCustomer() {
        CustomerData customerData = new CustomerData();
        customerData.generateRandomValidRequiredData(true, true);
        return createCustomer(customerData);
    }

    @Step("Find any customer")
    public CustomerData getAnyCustomer() {
        CustomerSearchFilters customerSearchFilters = new CustomerSearchFilters();
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);
        customerSearchFilters
                .setDiscriminantType(CustomerSearchFilters.DiscriminantType.PHONENUMBER);
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);
        customerSearchFilters.setDiscriminantValue("+71111111111");
        Response<CustomerListData> resp = customerClient.searchForCustomers(customerSearchFilters);
        assertThat("GetAnyCustomer Method. Response: " + resp.toString(), resp.isSuccessful());
        List<CustomerData> customers = resp.asJson().getItems();
        assertThat("GetAnyCustomer Method. Count of customers", customers,
                hasSize(greaterThan(0)));
        return customers.get(0);
    }

    @Step("Try to find phone number which is connected to no one customer")
    public String findUnusedPhoneNumber() {
        int attemptsCount = 10;

        CustomerSearchFilters customerSearchFilters = new CustomerSearchFilters();
        customerSearchFilters
                .setDiscriminantType(CustomerSearchFilters.DiscriminantType.PHONENUMBER);
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);

        for (int i = 0; i < attemptsCount; i++) {
            String phoneNumber = "+7" + RandomStringUtils.randomNumeric(10);
            customerSearchFilters.setDiscriminantValue(phoneNumber);
            Response<CustomerListData> resp = customerClient
                    .searchForCustomers(customerSearchFilters);
            if (resp.isSuccessful()) {
                if (resp.asJson().getItems().size() == 0) {
                    return phoneNumber;
                }
            } else {
                Log.error(resp.toString());
                return phoneNumber;
            }
        }
        Assert.fail("Couldn't find unused phone number for " + attemptsCount + " attempts");
        return null;
    }

}
