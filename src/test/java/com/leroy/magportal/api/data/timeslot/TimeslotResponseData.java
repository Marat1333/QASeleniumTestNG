package com.leroy.magportal.api.data.timeslot;

import java.util.List;
import lombok.Data;
import org.joda.time.DateTime;

@Data
public class TimeslotResponseData {

    private List<Slot> data;

    @Data
    private class Slot {

        private DateTime availableDate;
        private String storeId;
    }

}
