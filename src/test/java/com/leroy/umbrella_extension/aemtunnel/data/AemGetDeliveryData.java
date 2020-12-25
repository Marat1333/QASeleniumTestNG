package com.leroy.umbrella_extension.aemtunnel.data;

import java.util.ArrayList;
import lombok.Data;

@Data
public class AemGetDeliveryData {

    private String transactionId;
    private ArrayList<String> deliveryTo;
    private ArrayList<String> typeOfLift;
    private ArrayList<Delivery> deliveries;
    private Double totalWeight;

    @Data
    private class Delivery {

        private String deliveryMode;
        private boolean available;
    }
}
