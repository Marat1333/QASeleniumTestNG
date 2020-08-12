package com.leroy.magportal.api.requests;

import com.leroy.constants.api.OutputDocumentFormat;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v4/catalog/search")
public class GetCatalogSearch extends com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch {

    public GetCatalogSearch setOutputFormat(OutputDocumentFormat format){
        String paramName = "outputFormat";
        switch (format){
            case EXCEL:
                return (GetCatalogSearch) queryParam(paramName, "xls");
            case CSV:
                return (GetCatalogSearch) queryParam(paramName, "csv");
            default:
                return null;
        }
    }
}
