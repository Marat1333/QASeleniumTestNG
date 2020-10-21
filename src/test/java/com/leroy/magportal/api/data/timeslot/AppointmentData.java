package com.leroy.magportal.api.data.timeslot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AppointmentData {

    @JsonProperty("DEFAULT")
    private Boolean defaults;
    private String end;
    private String start;
}