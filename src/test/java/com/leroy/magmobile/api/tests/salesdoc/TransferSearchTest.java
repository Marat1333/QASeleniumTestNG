package com.leroy.magmobile.api.tests.salesdoc;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.TransferClient;
import com.leroy.magmobile.api.data.sales.transfer.TransferDataList;
import com.leroy.magmobile.api.data.sales.transfer.TransferProductOrderData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSalesDocData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSearchFilters;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import io.qameta.allure.Issue;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TransferSearchTest extends BaseProjectApiTest {

    private TransferClient client() {
        return apiClientProvider.getTransferClient();
    }

    private void verifyTypicalResponse(Response<TransferDataList> resp, TransferSearchFilters filters) {
        assertThat(resp, successful());
        TransferDataList transferDataList = resp.asJson();
        assertThat("items count", transferDataList.getItems(), hasSize(greaterThan(0)));
        assertThat("items total count", transferDataList.getTotalCount(),
                is(greaterThanOrEqualTo(transferDataList.getItems().size())));
        for (TransferSalesDocData transferSalesDocData : transferDataList.getItems()) {
            assertThat("task id", transferSalesDocData.getTaskId(), not(emptyOrNullString()));
            if (filters.getStatus() != null)
                assertThat("transfer doc status", transferSalesDocData.getStatus(), is(filters.getStatus()));
            else
                assertThat("transfer doc status", transferSalesDocData.getStatus(), not(emptyOrNullString()));
            assertThat("createdBy", transferSalesDocData.getTaskId(), not(emptyOrNullString()));
            assertThat("products", transferSalesDocData.getProducts(), hasSize(greaterThan(0)));
            for (TransferProductOrderData product : transferSalesDocData.getProducts()) {
                assertThat("Product lm codes are missing in the task #" + transferSalesDocData.getTaskId(),
                        product.getLmCode(), not(emptyOrNullString()));
                assertThat("Product titles are missing in the task #" + transferSalesDocData.getTaskId(),
                        product.getTitle(), not(emptyOrNullString()));
            }
        }
    }

    @Test(description = "C3272534 SalesDoc transfers GET with default params")
    public void testTransferTaskSearchWithDefaultParams() {
        TransferClient transferClient = client();

        TransferSearchFilters filters = new TransferSearchFilters();
        Response<TransferDataList> resp = transferClient.searchForTasks(filters);
        verifyTypicalResponse(resp, filters);
    }

    @Issue("BACKEND_ISSUE")
    @Test(description = "C3272535 SalesDoc transfers GET with status NEW")
    public void testTransferTaskSearchByStatusNew() {
        TransferClient transferClient = client();

        TransferSearchFilters filters = new TransferSearchFilters();
        filters.setStatus(SalesDocumentsConst.States.TRANSFER_NEW.getApiVal());
        Response<TransferDataList> resp = transferClient.searchForTasks(filters);
        verifyTypicalResponse(resp, filters);
    }

    @Test(description = "C3272536 SalesDoc transfers GET with status=CONFIRMED")
    public void testTransferTaskSearchByStatusConfirmed() {
        TransferClient transferClient = client();

        TransferSearchFilters filters = new TransferSearchFilters();
        filters.setStatus(SalesDocumentsConst.States.CONFIRMED.getApiVal());
        Response<TransferDataList> resp = transferClient.searchForTasks(filters);
        verifyTypicalResponse(resp, filters);
    }
}
