package com.leroy.magportal.api.data.timeslot;

import java.util.List;
import lombok.Data;

@Data
public class TimeslotPayload {

    private List<String> lmCodes;
    private Integer stores;
    private String date;
    private String customerType;
}