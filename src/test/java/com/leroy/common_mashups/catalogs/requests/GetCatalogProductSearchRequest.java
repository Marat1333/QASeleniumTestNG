package com.leroy.common_mashups.catalogs.requests;

import com.leroy.magmobile.api.enums.CatalogSearchFields;
import com.leroy.magmobile.api.enums.SortingOrder;
import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/products")
public class GetCatalogProductSearchRequest extends CommonSearchRequestBuilder<GetCatalogProductSearchRequest> {

    public GetCatalogProductSearchRequest setByLmCode(String val) {
        return queryParam("byLmCode", val);
    }

    public GetCatalogProductSearchRequest setByBarCode(String val) {
        return queryParam("byBarCode", val);
    }

    public GetCatalogProductSearchRequest setByName(String val) {
        return queryParam("byName", val);
    }

    public GetCatalogProductSearchRequest setByNameLike(String val) {
        return queryParam("byNameLike", val);
    }

    public GetCatalogProductSearchRequest setAvsDate(String val) {
        return queryParam("avsDate", val);
    }

    public GetCatalogProductSearchRequest setSubDepartmentId(String val) {
        return queryParam("subdepartmentId", val);
    }

    public GetCatalogProductSearchRequest setClassId(String val) {
        return queryParam("classId", val);
    }

    public GetCatalogProductSearchRequest setSubclassId(String val) {
        return queryParam("subclassId", val);
    }

    public GetCatalogProductSearchRequest setGamma(String val) {
        return queryParam("gamma", val);
    }

    public GetCatalogProductSearchRequest setCtm(Boolean val) {
        return queryParam("ctm", val);
    }

    public GetCatalogProductSearchRequest setTop1000(Boolean val) {
        return queryParam("top1000", val);
    }

    public GetCatalogProductSearchRequest setLimitedOffer(Boolean val) {
        return queryParam("limitedOffer", val);
    }

    public GetCatalogProductSearchRequest setOrderType(String val) {
        return queryParam("orderType", val);
    }

    public GetCatalogProductSearchRequest setBestPrice(Boolean val) {
        return queryParam("bestPrice", val);
    }

    // Example: 1,2,3,4
    public GetCatalogProductSearchRequest setGroupByDepartment(String val) {
        return queryParam("groupByDepartment", val);
    }

    public GetCatalogProductSearchRequest setTopEM(Boolean val) {
        if (val == null)
            return this;
        return queryParam("topEM", val);
    }

    public GetCatalogProductSearchRequest setSupCode(String val) {
        return queryParam("supCode", val);
    }

    public GetCatalogProductSearchRequest setSupId(String val) {
        return queryParam("supplierId", val);
    }

    public GetCatalogProductSearchRequest setHasAvailableStock(Boolean val) {
        if (val == null)
            return this;
        return queryParam("hasAvailableStock", val);
    }

    public GetCatalogProductSearchRequest setTop(String val) {
        return queryParam("top", val);
    }

    public GetCatalogProductSearchRequest setExtend(String val) {
        return queryParam("extend", val);
    }

    // Examples: lmCode|ASC
    //           lmCode|DESC
    public GetCatalogProductSearchRequest setSortBy(CatalogSearchFields field, SortingOrder sortingOrder) {
        return queryParam("sortBy", field.getName() + "|" + sortingOrder);
    }

}
