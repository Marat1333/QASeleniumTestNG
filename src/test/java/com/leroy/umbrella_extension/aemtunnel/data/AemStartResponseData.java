package com.leroy.umbrella_extension.aemtunnel.data;

import java.util.ArrayList;
import lombok.Data;

@Data
public class AemStartResponseData {

    private int contextStoreId;
    private int regionId;
    private ArrayList<Step> steps;
    private String transactionId;
    private String solutionId;
    private ArrayList<Delivery> deliveries;

    @Data
    private class Step {

        private String type;
        private String title;
    }

    @Data
    private class Delivery {

        private String deliveryMode;
        private boolean available;
        private boolean selected;
        private ArrayList<Product> availableProducts;
        private ArrayList<Product> unavailableProducts;
        private ArrayList<Object> availableConfigurations;
        private Double totalWeight;
        private TotalAmount totalAmount;
    }

    @Data
    private class Product {

        private String productId;
        private Double quantity;
        private Double stock;
        private Double price;
        private Double weight;
        private Double productQuantity;
        private Double productWeight;
    }

    @Data
    private class TotalAmount {

        private Double amountProducts;
        private Double amountDelivery;
        private Double amountLift;
        private Double totalAmount;
    }
}
