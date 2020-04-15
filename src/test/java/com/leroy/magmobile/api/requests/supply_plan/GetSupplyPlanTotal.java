package com.leroy.magmobile.api.requests.supply_plan;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/supplyPlan/supplyTotal")
public class GetSupplyPlanTotal extends BaseSupplyPlanRequest<GetSupplyPlanTotal> {
}
