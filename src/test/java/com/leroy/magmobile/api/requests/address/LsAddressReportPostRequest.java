package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/lsAddress/report")
public class LsAddressReportPostRequest extends CommonLegoRequest<LsAddressReportPostRequest> {
    public LsAddressReportPostRequest setShopId(int val) {
        return queryParam("shopId", val);
    }

    public LsAddressReportPostRequest setDepartmentId(int val) {
        return queryParam("departmentId", val);
    }
}
