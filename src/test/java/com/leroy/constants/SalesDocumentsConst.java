package com.leroy.constants;

public class SalesDocumentsConst {

    // Состояния документа
    public enum States {
        CREATED("", "Создан"),
        AUTO_PROCESSING("","Автообработка"),
        TRANSFORMED("","Преобразован"),
        CANCELLED("CANCELLED", "Отменен"),
        DRAFT("DRAFT","Черновик"),

        // Transfer
        NEW("NEW", ""),

        // Cart
        DELETED("DELETED", "");

        private String uiVal;
        private String apiVal;

        States(String apiVal, String uiVal) {
            this.apiVal = apiVal;
            this.uiVal = uiVal;
        }

        public String getUiVal() {
            return uiVal;
        }

        public String getApiVal() {
            return apiVal;
        }
    }

    // Типы документов
    public enum Types {
        SALE("SALE", ""),
        CART("CART","Корзина"),
        QUOTATION("QUOTATION","Смета"),
        ORDER("ORDER","");

        private String uiVal;
        private String apiVal;

        Types(String apiVal, String uiVal) {
            this.apiVal = apiVal;
            this.uiVal = uiVal;
        }

        public String getUiVal() {
            return uiVal;
        }

        public String getApiVal() {
            return apiVal;
        }
    }

    // Отзыв
    public enum GiveAwayPoints {
        SALES_FLOOR("SALESFLOOR", "Из торгового зала");

        private String uiVal;
        private String apiVal;

        GiveAwayPoints(String apiVal, String uiVal) {
            this.apiVal = apiVal;
            this.uiVal = uiVal;
        }

        public String getUiVal() {
            return uiVal;
        }

        public String getApiVal() {
            return apiVal;
        }
    }
}
