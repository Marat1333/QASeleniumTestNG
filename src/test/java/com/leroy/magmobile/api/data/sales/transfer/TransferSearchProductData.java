package com.leroy.magmobile.api.data.sales.transfer;

import lombok.Data;

import java.util.List;

@Data
public class TransferSearchProductData {
    private String lmCode;
    private String barCode;
    private String title;
    private Integer totalQuantity;
    private List<Source> source;
    private String altPrice;
    private String priceUnit;
    private String altPriceUnit;
    private Integer recommendedPrice;
    private String priceCurrency;
    private Double price;

    @Data
    public static class Source {
        private String type;
        private List<MonoPallet> monoPallets;
        private List<MixPallet> mixPallets;

        @Data
        public static class MonoPallet {
            private Integer quantity;
            private Integer capacity;
        }

        @Data
        public static class MixPallet {
            private String id;
            private Integer capacity;
        }
    }
}
