package com.leroy.umbrella_extension.aemtunnel.data;

import java.util.ArrayList;
import lombok.Data;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.TotalAmount;

@Data
public class AemCalculateResponseData {

    private TotalAmount totalAmount;
    private ArrayList<Appointment> appointments;

    @Data
    private class Appointment {

        private String date;
        private boolean available;
    }
}
