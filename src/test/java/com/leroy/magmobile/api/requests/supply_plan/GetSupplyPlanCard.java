package com.leroy.magmobile.api.requests.supply_plan;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/supplyPlan/supplyCard")
public class GetSupplyPlanCard  extends CommonSearchRequestBuilder<GetSupplyPlanCard> {

    public GetSupplyPlanCard setDocumentNo(String value){
        return queryParam("documentNo",value);
    }

    public GetSupplyPlanCard setDocumentType(String value){
        return queryParam("documentType",value);
    }

    public GetSupplyPlanCard setSendingLocation(String value){
        return queryParam("sendingLocation",value);
    }

    public GetSupplyPlanCard setSendingLocationType(String value){
        return queryParam("sendingLocationType",value);
    }
}
