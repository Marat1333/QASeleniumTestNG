package com.leroy.magmobile.api.requests;

public class CommonSearchRequestBuilder<J extends CommonSearchRequestBuilder<J>> extends CommonLegoRequest<J> {

    // Header

    // Query params

    public J setPageSize(Integer val) {
        if (val == null)
            return (J) this;
        return queryParam("pageSize", val);
    }

    public J setStartFrom(Integer val) {
        if (val == null)
            return (J) this;
        return queryParam("startFrom", val);
    }

    public J setLastItemId(String val) {
        return queryParam("lastItemId", val);
    }

    public J setPageNumber(Integer val) {
        return queryParam("page", val);
    }
}
