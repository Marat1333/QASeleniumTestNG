package com.leroy.magmobile.api.data.supply_plan.Card;

import lombok.Data;

import java.util.List;

@Data
public class SupplyCardData {
    private List<SupplyCardSendingLocationData> sendingLocation;
    private List<SupplyCardShipmentsData> shipments;
    private List<SupplyCardOtherProductsData> otherProducts;
}
