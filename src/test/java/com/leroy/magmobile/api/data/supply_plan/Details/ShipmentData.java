package com.leroy.magmobile.api.data.supply_plan.Details;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShipmentData)) return false;
        ShipmentData that = (ShipmentData) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(time, that.time) &&
                Objects.equals(rowType, that.rowType) &&
                Objects.equals(sendingLocation, that.sendingLocation) &&
                Objects.equals(sendingLocationType, that.sendingLocationType) &&
                Objects.equals(sendingLocationName, that.sendingLocationName) &&
                Objects.equals(documentType, that.documentType) &&
                Objects.equals(palletPlan, that.palletPlan) &&
                Objects.equals(palletFact, that.palletFact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, rowType, sendingLocation, sendingLocationType, sendingLocationName, documentType, palletPlan, palletFact);
    }
}
