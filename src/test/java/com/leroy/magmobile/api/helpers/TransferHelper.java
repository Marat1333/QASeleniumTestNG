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

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransferHelper extends ApiClientProvider {

    @Step("API: Создаем заявку на отзыв")
    public TransferSalesDocData createTransferTask(
            TransferProductOrderData productData, SalesDocumentsConst.GiveAwayPoints giveAwayPoints) {
        TransferClient transferClient = getTransferClient();

        TransferSalesDocData postSalesDocData = new TransferSalesDocData();
        postSalesDocData.setProducts(Collections.singletonList(productData));
        postSalesDocData.setShopId(Integer.valueOf(userSessionData().getUserShopId()));
        postSalesDocData.setDepartmentId(userSessionData().getUserDepartmentId());
        postSalesDocData.setDateOfGiveAway(ZonedDateTime.now());
        postSalesDocData.setPointOfGiveAway(giveAwayPoints.getApiVal());

        // Send request
        Response<TransferSalesDocData> resp = transferClient.sendRequestCreate(postSalesDocData);
        assertThat(resp, successful());
        return resp.asJson();
    }

}
