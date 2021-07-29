package com.leroy.magmobile.api.tests.salesdoc;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.TransferClient;
import com.leroy.magmobile.api.data.sales.transfer.TransferDataList;
import com.leroy.magmobile.api.data.sales.transfer.TransferProductOrderData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSalesDocData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSearchFilters;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import io.qameta.allure.Issue;
import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class TransferSearchTest extends BaseProjectApiTest {

    @Inject
    private TransferClient transferClient;

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
    @AllureId("13144")
    public void testTransferTaskSearchWithDefaultParams() {
        TransferSearchFilters filters = new TransferSearchFilters();
        Response<TransferDataList> resp = transferClient.searchForTasks(filters);
        verifyTypicalResponse(resp, filters);
    }

    @Issue("BACKEND_ISSUE")
    @Test(description = "C3272535 SalesDoc transfers GET with status NEW")
    @AllureId("13145")
    public void testTransferTaskSearchByStatusNew() {
        TransferSearchFilters filters = new TransferSearchFilters();
        filters.setStatus(SalesDocumentsConst.States.TRANSFER_NEW.getApiVal());
        Response<TransferDataList> resp = transferClient.searchForTasks(filters);
        verifyTypicalResponse(resp, filters);
    }

    @Test(description = "C3272536 SalesDoc transfers GET with status=CONFIRMED")
    @AllureId("13146")
    public void testTransferTaskSearchByStatusConfirmed() {
        TransferSearchFilters filters = new TransferSearchFilters();
        filters.setStatus(SalesDocumentsConst.States.CONFIRMED.getApiVal());
        Response<TransferDataList> resp = transferClient.searchForTasks(filters);
        verifyTypicalResponse(resp, filters);
    }
}
