package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.supply_plan.Card.SupplyCardData;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentDataList;
import com.leroy.magmobile.api.data.supply_plan.Total.TotalPalletDataList;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierDataList;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanCard;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanDetails;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanSuppliers;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanTotal;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SupplyPlanClient extends MagMobileClient {

    @Step("Get shipments")
    public Response<ShipmentDataList> getShipments(GetSupplyPlanDetails params) {
        return execute(params, ShipmentDataList.class);
    }

    @Step("Get Total Pallets")
    public Response<TotalPalletDataList> getTotalPallets(GetSupplyPlanTotal params) {
        return execute(params, TotalPalletDataList.class);
    }

    @Step("Get supply card")
    public Response<SupplyCardData> getSupplyCard(GetSupplyPlanCard params) {
        return execute(params, SupplyCardData.class);
    }

    @Step("Get suppliers")
    public Response<SupplierDataList> getSuppliers(GetSupplyPlanSuppliers params) {
        return execute(params, SupplierDataList.class);
    }
}
