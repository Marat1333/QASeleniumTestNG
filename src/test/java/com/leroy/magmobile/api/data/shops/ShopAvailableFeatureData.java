package com.leroy.magmobile.api.data.shops;

import lombok.Data;

@Data
public class ShopAvailableFeatureData {
    private Boolean pickup;
    private Boolean ruptures;
    private Boolean suppliesPlan;
    private Boolean lsrm;
    private Boolean estimate;
    private Boolean multicart;
    private Boolean em;
    private Boolean pickingTasks;
    private Boolean emSale;
    private Boolean highloadPosDisabled;

}
