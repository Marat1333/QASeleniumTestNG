package com.leroy.magportal.api.data.picking;

import lombok.Data;


@Data
public class PickingTaskProductsInfoData {

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