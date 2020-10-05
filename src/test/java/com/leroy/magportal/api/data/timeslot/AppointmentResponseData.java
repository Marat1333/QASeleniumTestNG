package com.leroy.magportal.api.data.timeslot;

import java.util.List;
import lombok.Data;

@Data
public class AppointmentResponseData {

    private Appointments data;

    @Data
    public class Appointments {

        private List<AppointmentData> appointments;
    }
}
