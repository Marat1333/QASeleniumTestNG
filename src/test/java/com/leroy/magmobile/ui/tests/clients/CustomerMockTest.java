package com.leroy.magmobile.ui.tests.clients;

import com.leroy.common_mashups.data.customer.CustomerSearchFilters;
import com.leroy.common_mashups.requests.customer.CustomerAccountGetRequest;
import com.leroy.common_mashups.requests.customer.CustomerAccountUpdateRequest;
import com.leroy.common_mashups.requests.customer.CustomerAccountsSearchRequest;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.UserSessionData;
import com.leroy.magmobile.api.requests.salesdoc.search.SalesDocSearchV3Get;
import com.leroy.magmobile.ui.pages.customers.*;
import com.leroy.magmobile.ui.pages.customers.data.PhoneUiData;
import com.leroy.magmobile.ui.tests.BaseUiMagMobMockTest;
import org.mbtest.javabank.http.predicates.PredicateType;
import org.testng.annotations.Test;

import java.util.Arrays;

public class CustomerMockTest extends BaseUiMagMobMockTest {

    @Override
    public UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData userSessionData = super.initTestClassUserSessionDataTemplate();
        userSessionData.setUserShopId(EnvConstants.BASIC_USER_SHOP_ID);
        userSessionData.setUserDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID);
        return userSessionData;
    }

    @Test(description = "C3201019 Редактирование данных клиента (физ. лицо)")
    public void testEditCustomer() throws Exception {
        String customerNumber = "1288687";
        String existedClientPhone = "+73201019000";
        String newPhoneNumber = "+73201019001";
        // Prepare mocks
        createStub(PredicateType.DEEP_EQUALS, new CustomerAccountsSearchRequest()
                .setCustomerType(CustomerSearchFilters.CustomerType.NATURAL)
                .setDiscriminantValue(existedClientPhone)
                .setDiscriminantType(CustomerSearchFilters.DiscriminantType.PHONENUMBER)
                .setShopId(getUserSessionData().getUserShopId())
                .setLdap(getUserSessionData().getUserLdap()), 0);
        createStub(PredicateType.DEEP_EQUALS, new CustomerAccountGetRequest()
                .setLdap(getUserSessionData().getUserLdap())
                .setShopId(getUserSessionData().getUserShopId())
                .setCustomerNumber(customerNumber), 1);
        createStub(PredicateType.DEEP_EQUALS, new CustomerAccountUpdateRequest()
                .setLdap(getUserSessionData().getUserLdap())
                .setShopId(getUserSessionData().getUserShopId()), 2);
        createStub(PredicateType.DEEP_EQUALS, new SalesDocSearchV3Get()
                .setStartFrom(1)
                .setDocType(SalesDocumentsConst.Types.ORDER.getApiVal())
                .setPageSize(1)
                .setPageNumber(1).setCustomerNumber(customerNumber), 3);

        MainCustomerPage mainCustomerPage = loginAndGoTo(MainCustomerPage.class);
        SearchCustomerPage searchCustomerPage = mainCustomerPage.clickSearchCustomerField();
        searchCustomerPage.searchCustomerByPhone(existedClientPhone, true);
        ViewCustomerPage viewCustomerPage = new ViewCustomerPage()
                .verifyRequiredElements();

        // Step 1
        step("Нажмите на личные данные");
        PersonalInfoPage personalInfoPage = viewCustomerPage.goToPersonalData()
                .verifyRequiredElements();

        // Step 2
        step("Нажмите на кнопку редактирования клиента");
        EditCustomerInfoPage editCustomerInfoPage = personalInfoPage.clickEditButton();

        // Step 4 - 7
        step("Добавьте новый рабочий телефон клиенту");
        PhoneUiData phoneData = new PhoneUiData()
                .setPhoneNumber(newPhoneNumber)
                .setType(PhoneUiData.Type.WORK);
        editCustomerInfoPage.editPhoneNumber(2, phoneData, true);

        // Step 8
        step("Нажмите на Сохранить");
        personalInfoPage = editCustomerInfoPage.clickSaveButton();
        personalInfoPage.shouldPhoneNumbersAre(Arrays.asList(existedClientPhone, newPhoneNumber))
                .shouldPhoneTypesAre(Arrays.asList(PhoneUiData.Type.MAIN, PhoneUiData.Type.WORK));
    }

    @Test(description = "C22762927 Просмотр данных клиента (юр. лицо)")
    public void testViewLegalClientData() throws Exception {
        // Test data
        String orgName = "Рога и копыта";
        String orgCard = "93010092991969072";
        String orgAddress = "город Мытищи, Московская область 141006";
        // Prepare Mock
        createStub(PredicateType.DEEP_EQUALS, new CustomerAccountsSearchRequest()
                .setCustomerType(CustomerSearchFilters.CustomerType.LEGAL)
                .setDiscriminantValue(orgCard)
                .setDiscriminantType(CustomerSearchFilters.DiscriminantType.LOYALTY_CARD_NUMBER)
                .setShopId(getUserSessionData().getUserShopId())
                .setLdap(getUserSessionData().getUserLdap()), 0);

        // Pre-condition
        MainCustomerPage mainCustomerPage = loginAndGoTo(MainCustomerPage.class);
        SearchCustomerPage searchCustomerPage = mainCustomerPage.clickSearchCustomerField();
        searchCustomerPage.searchLegalCustomerByCardNumber(orgCard);

        // Step 1
        step("Нажмите на Данные организации");
        OrganizationInfoPage organizationInfoPage = new ViewCustomerPage().goToOrganizationData();
        organizationInfoPage.shouldOrgNameIs(orgName)
                .shouldOrgCardIs(orgCard)
                .shouldOrgAddressIs(orgAddress);
    }

}
