package com.leroy.magportal.api.data.timeslot;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class AppointmentPayload extends TimeslotPayload{

    private String referenceStoreId;
    private String deliveryId;
}