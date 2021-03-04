package com.leroy.magportal.api.data.timeslot;

import java.util.List;
import lombok.Data;

@Data
public class AppointmentPayload {

    private String referenceStoreId;
    private String deliveryId;
    private List<String> lmCodes;
    private List<String> stores;
    private String date;
    private String customerType;
}