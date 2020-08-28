package com.leroy.magmobile.api.helpers;


import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.ApiClientProvider;
import com.leroy.magmobile.api.clients.TransferClient;
import com.leroy.magmobile.api.data.sales.transfer.TransferProductOrderData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSalesDocData;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransferHelper extends ApiClientProvider {

    @Step("API: Создаем заявку на отзыв")
    public TransferSalesDocData createTransferTask(
            List<TransferProductOrderData> productDataList, SalesDocumentsConst.GiveAwayPoints giveAwayPoints) {
        TransferClient transferClient = getTransferClient();
        TransferSalesDocData postSalesDocData = new TransferSalesDocData();
        postSalesDocData.setProducts(productDataList);
        postSalesDocData.setShopId(Integer.valueOf(userSessionData().getUserShopId()));
        postSalesDocData.setDepartmentId(userSessionData().getUserDepartmentId());
        postSalesDocData.setDateOfGiveAway(ZonedDateTime.now());
        postSalesDocData.setPointOfGiveAway(giveAwayPoints.getApiVal());

        // Send request
        Response<TransferSalesDocData> resp = transferClient.sendRequestCreate(postSalesDocData);
        assertThat(resp, successful());
        return resp.asJson();
    }

    public TransferSalesDocData createTransferTask(
            TransferProductOrderData productData, SalesDocumentsConst.GiveAwayPoints giveAwayPoints) {
        return createTransferTask(Collections.singletonList(productData), giveAwayPoints);
    }

}
