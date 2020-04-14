package com.leroy.magmobile.api.tests.cusomers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.CustomerClient;
import com.leroy.magmobile.api.data.customer.CustomerData;
import com.leroy.magmobile.api.data.customer.CustomerResponseBodyData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CustomerTest extends BaseProjectApiTest {

    @Inject
    private CustomerClient customerClient;

    private CustomerData customerData;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @BeforeClass
    private void setUp() {
        customerClient.setSessionData(sessionData);
    }

    @Test(description = "C3164926 Create Customer - happy path")
    public void testCreateCustomer() {
        CustomerData newCustomerData = new CustomerData();
        newCustomerData.generateRandomValidRequiredData(true);
        Response<CustomerResponseBodyData> resp = customerClient.createCustomer(newCustomerData);
        customerData = customerClient.assertThatIsCreatedAndGetData(resp, newCustomerData);
    }

    @Test(description = "C23194970 Get Customer")
    public void testGetCustomer() {
        if (customerData == null)
            throw new IllegalArgumentException("customerData hasn't been created");
        Response<CustomerResponseBodyData> resp = customerClient.getCustomer(customerData.getCustomerNumber());
        customerClient.assertThatGetResponseMatches(resp, customerData);
    }

    @Test(description = "C23194971 Update Customer - change gender")
    public void testUpdateCustomer() {
        if (customerData == null)
            throw new IllegalArgumentException("customerData hasn't been created");
        customerData.setGender(customerData.getGender().equals("male") ? "female" : "male");
        Response<CustomerResponseBodyData> resp = customerClient.updateCustomer(customerData);
        customerClient.assertThatIsCreatedAndGetData(resp, customerData);
    }

    // Negative scenarios

    @Test(description = "C23194972 Get Customer Balance  for non-existent account")
    public void testGetCustomerBalanceForNonExistentAccount() {
        String nonExistentAccountNumber = "999999";
        Response<JsonNode> resp = customerClient.getCustomerBalance(nonExistentAccountNumber);
        customerClient.assertThatBalanceNotFound(resp);
    }

}
