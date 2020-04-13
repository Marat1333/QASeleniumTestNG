package com.leroy.magmobile.api.data.supply_plan.Details;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ShipmentData {

    private LocalDate date;
    private String time;
    private String rowType;
    private String sendingLocation;
    private String sendingLocationType;
    private String sendingLocationName;
    private JsonNode documentType;
    private JsonNode documentNo;
    private JsonNode shipmentNo;
    private JsonNode rowStatus;
    private Integer palletPlan;
    private Integer palletFact;
    private String logFlow;
    private Boolean isFullReceived;
}
