package com.leroy.magportal.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.api.OutputDocumentFormat;
import com.leroy.magportal.api.requests.GetCatalogSearch;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CatalogSearchClient extends com.leroy.magmobile.api.clients.CatalogSearchClient {
    public Response<JsonNode> getDefaultSearchExcelOutput(){
        GetCatalogSearch param = new GetCatalogSearch();
        param.setOutputFormat(OutputDocumentFormat.EXCEL)
        .setPageSize(MAX_PAGE_SIZE)
        .setShopId(userSessionData.getUserShopId());
        return execute(param, JsonNode.class);
    }
}
