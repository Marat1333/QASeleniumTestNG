package com.leroy.magmobile.api.requests;

public class CommonSearchRequestBuilder<J extends CommonSearchRequestBuilder<J>> extends CommonLegoRequest<J> {

    // Header

    // Query params

    public J setPageSize(Integer val) {
        return queryParam("pageSize", val);
    }

    public J setStartFrom(Integer val) {
        return queryParam("startFrom", val);
    }

}
