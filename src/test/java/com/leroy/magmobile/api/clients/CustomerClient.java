package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.StatusCodes;
import com.leroy.magmobile.api.data.customer.Communication;
import com.leroy.magmobile.api.data.customer.CustomerData;
import com.leroy.magmobile.api.data.customer.CustomerListData;
import com.leroy.magmobile.api.requests.customer.*;
import com.leroy.magmobile.api.data.customer.CustomerResponseBodyData;
import com.leroy.magmobile.api.data.customer.CustomerSearchFilters;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CustomerClient extends MagMobileClient {

    /**
     * ---------- Executable Requests -------------
     **/

    @Step("Search for customers")
    public Response<CustomerListData> searchForCustomers(CustomerSearchFilters filters) {
        CustomerAccountsSearchRequest req = new CustomerAccountsSearchRequest();
        req.setCustomerType(filters.getCustomerType());
        req.setDiscriminantType(filters.getDiscriminantType());
        req.setDiscriminantValue(filters.getDiscriminantValue());
        req.setShopId(sessionData.getUserShopId());
        return execute(req, CustomerListData.class);
    }

    @Step("Create customer")
    public Response<CustomerResponseBodyData> createCustomer(CustomerData customerData) {
        CustomerAccountCreateRequest req = new CustomerAccountCreateRequest();
        req.setShopId(sessionData.getUserShopId());
        req.setLdap(sessionData.getUserLdap());
        req.jsonBody(customerData);
        return execute(req, CustomerResponseBodyData.class);
    }

    @Step("Update customer")
    public Response<CustomerResponseBodyData> updateCustomer(CustomerData customerData) {
        CustomerAccountUpdateRequest req = new CustomerAccountUpdateRequest();
        req.setShopId(sessionData.getUserShopId());
        req.setLdap(sessionData.getUserLdap());
        req.jsonBody(customerData);
        return execute(req, CustomerResponseBodyData.class);
    }

    @Step("Get customer with customerNumber={customerNumber}")
    public Response<CustomerResponseBodyData> getCustomer(String customerNumber) {
        CustomerAccountGetRequest req = new CustomerAccountGetRequest();
        req.setCustomerNumber(customerNumber);
        return execute(req, CustomerResponseBodyData.class);
    }

    @Step("Get customer balance for customer with customerNumber={customerNumber}")
    public Response<JsonNode> getCustomerBalance(String customerNumber) {
        CustomerAccountBalanceRequest req = new CustomerAccountBalanceRequest();
        req.setCustomerNumber(customerNumber);
        return execute(req, JsonNode.class);
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

    @Step("Check that customer is created and response body matches expectedCustomerData")
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

    @Step("Check that response body matches expectedData")
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

    // Negative verifications

    @Step("Check that Customer balance not found")
    public void assertThatBalanceNotFound(Response<JsonNode> resp) {
        assertThat(resp.toString(), resp.getStatusCode(), is(StatusCodes.ST_404_NOT_FOUND));
        assertThat("Error text is wrong", resp.asJson().get("error").asText(),
                is("Customer balance not found!"));

    }
}
