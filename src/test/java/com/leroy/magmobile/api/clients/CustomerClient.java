package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.customer.Communication;
import com.leroy.magmobile.api.data.customer.CustomerData;
import com.leroy.magmobile.api.data.customer.CustomerListData;
import com.leroy.magmobile.api.data.sales.cart_estimate.EstimateData;
import com.leroy.magmobile.api.requests.customer.CustomerAccountCreateRequest;
import com.leroy.magmobile.api.requests.customer.CustomerAccountGetRequest;
import com.leroy.magmobile.api.requests.customer.CustomerAccountsSearchRequest;
import com.leroy.magmobile.api.data.customer.CustomerResponseBodyData;
import com.leroy.magmobile.api.data.customer.CustomerSearchFilters;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CustomerClient extends MagMobileClient {

    /**
     * ---------- Executable Requests -------------
     **/

    public Response<CustomerListData> searchForCustomers(CustomerSearchFilters filters) {
        CustomerAccountsSearchRequest req = new CustomerAccountsSearchRequest();
        req.setCustomerType(filters.getCustomerType());
        req.setDiscriminantType(filters.getDiscriminantType());
        req.setDiscriminantValue(filters.getDiscriminantValue());
        req.setShopId(sessionData.getUserShopId());
        return execute(req, CustomerListData.class);
    }

    public Response<CustomerResponseBodyData> createCustomer(CustomerData customerData) {
        CustomerAccountCreateRequest req = new CustomerAccountCreateRequest();
        req.setShopId(sessionData.getUserShopId());
        req.setLdap(sessionData.getUserLdap());
        req.jsonBody(customerData);
        return execute(req, CustomerResponseBodyData.class);
    }

    public Response<CustomerResponseBodyData> getCustomer(String customerNumber) {
        CustomerAccountGetRequest req = new CustomerAccountGetRequest();
        req.setCustomerNumber(customerNumber);
        return execute(req, CustomerResponseBodyData.class);
    }

    // ----------------- VERIFICATIONS --------------- //

    public CustomerData assertThatIsCreatedAndGetData(Response<CustomerResponseBodyData> resp) {
        return assertThatIsCreatedAndGetData(resp, null);
    }

    private void shortVerificationCommunications(
            int i, Communication actualCommunication, Communication expectedCommunication) {
        assertThat("Communication #" + i + " - id", actualCommunication.getId(), notNullValue());
        assertThat("Communication #" + i + " - type", actualCommunication.getType(),
                is(expectedCommunication.getType()));
        assertThat("Communication #" + i + " - value", actualCommunication.getValue(),
                is(expectedCommunication.getValue()));
        assertThat("Communication #" + i + " - isMain", actualCommunication.getIsMain(),
                is(expectedCommunication.getIsMain()));

    }

    public CustomerData assertThatIsCreatedAndGetData(Response<CustomerResponseBodyData> resp,
                                                      CustomerData expectedCustomerData) {
        assertThatResponseIsOk(resp);
        CustomerResponseBodyData d = resp.asJson();
        assertThat("Entity in response body after creating customer account",
                d.getEntity(), notNullValue());
        CustomerData actualCustomerData = d.getEntity();
        assertThat("customerType", actualCustomerData.getCustomerType(), not(emptyOrNullString()));
        assertThat("customerNumber", actualCustomerData.getCustomerNumber(), not(emptyOrNullString()));

        if (expectedCustomerData != null) {
            assertThat("gender", actualCustomerData.getGender(), is(expectedCustomerData.getGender()));
            assertThat("firstName", actualCustomerData.getFirstName(), is(expectedCustomerData.getFirstName()));
            assertThat("lastName", actualCustomerData.getLastName(), is(expectedCustomerData.getLastName()));

            List<Communication> actualCommunicationList = actualCustomerData.getCommunications();
            List<Communication> expectedCommunicationList = expectedCustomerData.getCommunications();
            assertThat("communications size", actualCommunicationList, hasSize(expectedCommunicationList.size()));
            for (int i = 0; i < actualCommunicationList.size(); i++) {
                shortVerificationCommunications(i, actualCommunicationList.get(i), expectedCommunicationList.get(i));
            }
        }
        return actualCustomerData;
    }

    public CustomerClient assertThatGetResponseMatches(Response<CustomerResponseBodyData> resp, CustomerData expectedData) {
        assertThatResponseIsOk(resp);
        CustomerResponseBodyData d = resp.asJson();
        assertThat("Entity in response body after creating customer account",
                d.getEntity(), notNullValue());
        CustomerData actualData = d.getEntity();
        assertThat("customerType", actualData.getCustomerType(), is(expectedData.getCustomerType()));
        assertThat("customerNumber", actualData.getCustomerNumber(), is(expectedData.getCustomerNumber()));
        assertThat("gender", actualData.getGender(), is(expectedData.getGender()));
        assertThat("firstName", actualData.getFirstName(), is(expectedData.getFirstName()));
        assertThat("lastName", actualData.getLastName(), is(expectedData.getLastName()));

        // Communications:
        List<Communication> actualCommunicationList = actualData.getCommunications();
        List<Communication> expectedCommunicationList = expectedData.getCommunications();
        assertThat("communications size", actualCommunicationList, hasSize(expectedCommunicationList.size()));
        for (int i = 0; i < actualCommunicationList.size(); i++) {
            shortVerificationCommunications(i, actualCommunicationList.get(i), expectedCommunicationList.get(i));
            assertThat("Communication #" + i + " - communicationOrder",
                    actualCommunicationList.get(i).getCommunicationOrder(),
                    is(expectedCommunicationList.get(i).getCommunicationOrder()));
            assertThat("Communication #" + i + " - phoneType",
                    actualCommunicationList.get(i).getPhoneType(),
                    is(expectedCommunicationList.get(i).getPhoneType()));
            assertThat("Communication #" + i + " - goneAway",
                    actualCommunicationList.get(i).getGoneAway(),
                    is(expectedCommunicationList.get(i).getGoneAway()));
        }

        return this;
    }

    // Help Methods

    public CustomerData getAnyCustomer() {
        CustomerSearchFilters customerSearchFilters = new CustomerSearchFilters();
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);
        customerSearchFilters.setDiscriminantType(CustomerSearchFilters.DiscriminantType.PHONENUMBER);
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);
        customerSearchFilters.setDiscriminantValue("+71111111111");
        Response<CustomerListData> resp = searchForCustomers(customerSearchFilters);
        assertThatResponseIsOk(resp);
        List<CustomerData> customers = resp.asJson().getItems();
        assertThat("GetAnyCustomer Method. Count of customers", customers,
                hasSize(greaterThan(0)));
        return customers.get(0);
    }
}
