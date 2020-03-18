package com.leroy.magmobile.api.tests.cusomers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.magmobile.api.clients.CustomerClient;
import com.leroy.magmobile.api.data.customer.CustomerListData;
import com.leroy.magmobile.api.data.customer.CustomerSearchFilters;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CustomerSearchTest extends BaseProjectApiTest {

    @Override
    protected boolean isNeedAccessToken() {
        return false;
    }

    @Inject
    private Provider<CustomerClient> customerClientProvider;

    private CustomerClient customerClient() {
        CustomerClient client = customerClientProvider.get();
        client.setSessionData(sessionData);
        return client;
    }

    @Test(description = "Simple Search Customers")
    public void testSimpleSearchCustomers() {
        CustomerSearchFilters customerSearchFilters = new CustomerSearchFilters();
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);
        customerSearchFilters.setDiscriminantType(CustomerSearchFilters.DiscriminantType.PHONENUMBER);
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);
        customerSearchFilters.setDiscriminantValue("+71111111111");
        Response<CustomerListData> data = customerClient().searchForCustomers(customerSearchFilters);
        // TODO
        String s = "";
    }
}
