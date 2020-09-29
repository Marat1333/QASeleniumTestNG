package com.leroy.magportal.api.data.timeslot;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import org.joda.time.DateTime;

@Data
public class AppointmentResponseData {

    private Appointments data;

    @Data
    private class Appointments {

        private List<Appointment> appointments;
    }

    @Data
    private class Appointment {

        @JsonProperty("DEFAULT")
        private Boolean defaults;
        private DateTime end;
        private DateTime start;
    }

}
