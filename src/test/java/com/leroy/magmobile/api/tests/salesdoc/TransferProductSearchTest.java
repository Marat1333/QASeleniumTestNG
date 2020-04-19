package com.leroy.magmobile.api.tests.salesdoc;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.TransferClient;
import com.leroy.magmobile.api.data.sales.transfer.TransferSearchProductData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSearchProductDataList;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TransferProductSearchTest extends BaseProjectApiTest {

    private TransferClient client() {
        return apiClientProvider.getTransferClient();
    }

    @Test(description = "C3255521 Transfer product search GET")
    public void testTransferProductSearchGet() {
        Response<TransferSearchProductDataList> resp = client().searchForTransferProducts(
                SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR);
        isResponseOk(resp);
        TransferSearchProductDataList dataList = resp.asJson();
        assertThat("total count", dataList.getTotalCount(), greaterThan(0));
        assertThat("filtered count", dataList.getTotalCount(), greaterThan(0));
        assertThat("count of items", dataList.getItems(), hasSize(greaterThan(0)));
        for (TransferSearchProductData productData : dataList.getItems()) {
            assertThat("count of items", productData.getLmCode(), not(emptyOrNullString()));
        }
    }

}