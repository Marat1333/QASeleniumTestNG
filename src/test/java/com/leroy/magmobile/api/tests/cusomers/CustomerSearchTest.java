package com.leroy.magmobile.api.tests.cusomers;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import com.google.inject.Inject;
import com.leroy.common_mashups.customer_accounts.clients.CustomerClient;
import com.leroy.common_mashups.customer_accounts.data.Communication;
import com.leroy.common_mashups.customer_accounts.data.CustomerData;
import com.leroy.common_mashups.customer_accounts.data.CustomerListData;
import com.leroy.common_mashups.customer_accounts.data.CustomerSearchFilters;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CustomerSearchTest extends BaseProjectApiTest {

    @Inject
    private CustomerClient customerClient;

    @Test(description = "C23194969 Simple Search Customers by Phone")
    public void testSimpleSearchCustomers() {
        CustomerSearchFilters customerSearchFilters = new CustomerSearchFilters();
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);
        customerSearchFilters.setDiscriminantType(CustomerSearchFilters.DiscriminantType.PHONENUMBER);
        customerSearchFilters.setDiscriminantValue("+71111111111");
        Response<CustomerListData> resp = customerClient
                .searchForCustomers(customerSearchFilters);
        assertThat(resp, successful());
        CustomerListData data = resp.asJson();
        assertThat("Count of searched items", data.getItems(), hasSize(greaterThan(0)));
        for (CustomerData customerData : data.getItems()) {
            assertThat("customer type", customerData.getCustomerType(),
                    equalToIgnoringCase(customerSearchFilters.getCustomerType().name()));
            boolean phoneMatched = false;
            for (Communication communication : customerData.getCommunications()) {
                if (communication.getValue().equals(customerSearchFilters.getDiscriminantValue())) {
                    phoneMatched = true;
                    break;
                }
            }
            assertThat(String.format("Customer №%s don't have phone number: %s",
                    customerData.getCustomerNumber(),
                    customerSearchFilters.getDiscriminantValue()), phoneMatched);
        }
    }
}
