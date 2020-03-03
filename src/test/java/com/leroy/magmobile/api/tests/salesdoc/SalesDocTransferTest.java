package com.leroy.magmobile.api.tests.salesdoc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.SessionData;
import com.leroy.magmobile.api.helpers.FindTestDataHelper;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.sales.transfer.TransferProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.sales.transfer.TransferSalesDocData;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.PostSalesDocTransfer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDate;
import java.util.Collections;


public class SalesDocTransferTest extends BaseProjectTest {

    @Inject
    private Provider<MagMobileClient> magMobileClient;

    @Inject
    private AuthClient authClient;

    @BeforeClass
    private void setUpDefaultSessionData() {
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId("35");
        sessionData.setUserDepartmentId("1");
        //sessionData.setAccessToken(authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP,
        //        EnvConstants.BASIC_USER_PASS));
    }

    @Test
    public void test() {
        String productLmCode = FindTestDataHelper.getProductLmCodes(magMobileClient.get(),
                sessionData.getUserShopId(), 1).get(0);

        TransferProductOrderData productOrderData = new TransferProductOrderData();
        productOrderData.setLmCode(productLmCode);

        TransferSalesDocData salesDocData = new TransferSalesDocData();
        salesDocData.setProducts(Collections.singletonList(productOrderData));
        salesDocData.setShopId(sessionData.getUserShopId());
        salesDocData.setDepartmentId(sessionData.getUserDepartmentId());
        salesDocData.setDateOfGiveAway(LocalDate.now());

        PostSalesDocTransfer params = new PostSalesDocTransfer();
        params.setLdap(sessionData.getUserLdap());
        params.jsonBody(salesDocData);

        Response<TransferSalesDocData> resp = magMobileClient.get().createSalesDocTransfer(params);

        String s = "";
    }

}
