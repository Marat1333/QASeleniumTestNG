package com.leroy.magportal.api.data.timeslot;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class TimeslotPayload {

    private List<String> lmCodes;
    private List<String> stores;
    private String date;
}