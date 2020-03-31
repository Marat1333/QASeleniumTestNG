package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentDataList;
import com.leroy.magmobile.api.data.supply_plan.Card.SupplyCardData;
import com.leroy.magmobile.api.data.supply_plan.Total.TotalPalletDataList;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierDataList;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanCard;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanDetails;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanSuppliers;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanTotal;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SupplyPlanClient extends MagMobileClient{

    public Response <ShipmentDataList> getShipments(GetSupplyPlanDetails params){
        return execute(params, ShipmentDataList.class);
    }

    public Response <TotalPalletDataList> getTotalPallet(GetSupplyPlanTotal params){
        return execute(params, TotalPalletDataList.class);
    }

    public Response<SupplyCardData> getSupplyCard(GetSupplyPlanCard params){
        return execute(params, SupplyCardData.class);
    }

    public Response<SupplierDataList> getSupplier(GetSupplyPlanSuppliers params){
        return execute(params, SupplierDataList.class);
    }
}
