package com.leroy.magportal.api.data.onlineOrders;

import lombok.Data;


@Data
public class OrderProductsInfoData {

    private String lmCode;
    private ProductInfoStocks stocks;
    private Double warehouseStock;
    private Integer top;
    private Boolean topEm;
    private Integer supCode;

    @Data
    public static class ProductInfoStocks {

        private String EM;
        private String RM;
        private String RD;
    }
}