package com.leroy.magportal.api.data.onlineOrders;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.leroymerlin.qa.core.clients.customerorders.data.ShipTo;

@EqualsAndHashCode(callSuper = true)
@Data
public class ShipToData extends ShipTo {

    private String gpsX;//longitude
    private String gpsY;//latitude
}
