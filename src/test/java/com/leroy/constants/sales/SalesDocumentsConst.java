package com.leroy.constants.sales;

public class SalesDocumentsConst {

    // Состояния документа
    public enum States {
        CONFIRMED("CONFIRMED", "Создан"),
        ALLOWED_FOR_PICKING("ALLOWED_FOR_PICKING", "Готов к сборке"),
        IN_PROGRESS("CONFIRMATION_IN_PROGRESS", "Автообработка"),
        TRANSFORMED("", "Преобразован"),
        DELETED("DELETED", ""),
        CANCELLED("CANCELLED", "Отменен"),
        DRAFT("DRAFT", "Черновик"),

        // Transfer
        NEW("NEW", "");

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
        CART("CART", "Корзина"),
        ESTIMATE("QUOTATION", "Смета"),
        ORDER("ORDER", "");

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

    // Приоритет
    public enum Priorities {
        HIGH("HIGH", "");

        private String uiVal;
        private String apiVal;

        Priorities(String apiVal, String uiVal) {
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
        SALES_FLOOR("SALESFLOOR", "Из торгового зала"),
        PICKUP("PICKUP", "Самовывоз"),
        DELIVERY("", "Доставка");

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
