package com.leroy.magmobile.api.data.sales.transfer;

import lombok.Data;

import java.util.List;

@Data
public class TransferSearchProductData {
    private String lmCode;
    private Integer totalQuantity;
    private List<Source> source;
    private String altPrice;
    private String priceUnit;
    private String altPriceUnit;
    private Integer recommendedPrice;
    private String priceCurrency;
    private Integer price;

    @Data
    public static class Source {
        private String type;
        private List<MonoPallet> monoPallets;

        @Data
        public static class MonoPallet {
            private Integer quantity;
            private Integer capacity;
        }
    }
}
