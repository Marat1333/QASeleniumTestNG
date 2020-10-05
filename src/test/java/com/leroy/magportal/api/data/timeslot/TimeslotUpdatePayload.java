package com.leroy.magportal.api.data.timeslot;

import java.util.List;
import lombok.Data;

@Data
public class TimeslotUpdatePayload {

    private String availableDate;
    private String fulfillmentTaskId;
    private List<String> lmCodes;
    private List<String> stores;
}