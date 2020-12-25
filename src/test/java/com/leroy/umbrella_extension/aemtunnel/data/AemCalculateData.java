package com.leroy.umbrella_extension.aemtunnel.data;

import lombok.Data;

@Data
public class AemCalculateData {

    private String deliveryTo;
    private String typeOfLift;
    private Address address;
    private boolean craneArm;
    private Double distance;

    @Data
    public class Address {

        private String latitude;
        private String longitude;
        private String floor;
    }
}
