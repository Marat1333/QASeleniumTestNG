package com.leroy.magportal.api.data.timeslot;

import lombok.Data;

@Data
public class AppointmentPayload extends TimeslotPayload {

    private String referenceStoreId;
    private String deliveryId;
}