package com.leroy.magmobile.api.requests.catalog_search;

import com.leroy.magmobile.api.enums.CatalogSearchFields;
import com.leroy.magmobile.api.enums.SortingOrder;
import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "GET", path = "/v4/catalog/search")
public class GetCatalogSearch extends CommonSearchRequestBuilder<GetCatalogSearch> {

    public GetCatalogSearch setByLmCode(String val) {
        return queryParam("byLmCode", val);
    }

    public GetCatalogSearch setByBarCode(String val) {
        return queryParam("byBarCode", val);
    }

    public GetCatalogSearch setByName(String val) {
        return queryParam("byName", val);
    }

    public GetCatalogSearch setByNameLike(String val) {
        return queryParam("byNameLike", val);
    }

    public GetCatalogSearch setAvsDate(String val) {
        return queryParam("avsDate", val);
    }

    public GetCatalogSearch setSubDepartmentId(String val) {
        return queryParam("subdepartmentId", val);
    }

    public GetCatalogSearch setClassId(String val) {
        return queryParam("classId", val);
    }

    public GetCatalogSearch setSubclassId(String val) {
        return queryParam("subclassId", val);
    }

    public GetCatalogSearch setGamma(String val) {
        return queryParam("gamma", val);
    }

    public GetCatalogSearch setCtm(Boolean val) {
        return queryParam("ctm", val);
    }

    public GetCatalogSearch setTop1000(Boolean val) {
        return queryParam("top1000", val);
    }

    public GetCatalogSearch setLimitedOffer(Boolean val) {
        return queryParam("limitedOffer", val);
    }

    public GetCatalogSearch setOrderType(String val) {
        return queryParam("orderType", val);
    }

    public GetCatalogSearch setBestPrice(Boolean val) {
        return queryParam("bestPrice", val);
    }

    // Example: 1,2,3,4
    public GetCatalogSearch setGroupByDepartment(String val) {
        return queryParam("groupByDepartment", val);
    }

    public GetCatalogSearch setTopEM(Boolean val) {
        return queryParam("topEM", val);
    }

    public GetCatalogSearch setSupCode(String val) {
        return queryParam("supCode", val);
    }

    public GetCatalogSearch setSupId(String val) {
        return queryParam("supplierId", val);
    }

    public GetCatalogSearch setHasAvailableStock(Boolean val) {
        return queryParam("hasAvailableStock", val);
    }

    public GetCatalogSearch setTop(String val) {
        return queryParam("top", val);
    }

    public GetCatalogSearch setExtend(String val) {
        return queryParam("extend", val);
    }

    // Examples: lmCode|ASC
    //           lmCode|DESC
    public GetCatalogSearch setSortBy(CatalogSearchFields field, SortingOrder sortingOrder) {
        return queryParam("sortBy", field.getName() + "|" + sortingOrder);
    }

}
