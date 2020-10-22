package com.leroy.constants.sales;

public class SalesDocumentsConst {

    // Состояния документа
    public enum States {
        CONFIRMED("CONFIRMED", "Создан"),
        WAITING_FOR_PAYMENT("WAITING_FOR_PAYMENT", "Ожидает оплату"),
        ALLOWED_FOR_PICKING("ALLOWED_FOR_PICKING", "Готов к сборке"),
        IN_PROGRESS("CONFIRMATION_IN_PROGRESS", "Автообработка"),
        PICKING_IN_PROGRESS("PICKING_IN_PROGRESS", "Сборка"),
        PICKED("PICKED", "Собран"),
        PARTIALLY_PICKED("PARTIALLY_PICKED", "Част. собран"),
        PICKED_WAIT("PICKED_WAIT", "Собран: Ожидает списания"),
        ALLOWED_FOR_GIVEAWAY("ALLOWED_FOR_GIVEAWAY", "Собран"),
        PARTIALLY_GIVEN_AWAY("PARTIALLY_GIVEN_AWAY", "Част. выдан"),
        GIVEN_AWAY("GIVEN_AWAY", "Выдан"),
        SHIPPED("SHIPPED", "Отгружен"),
        ON_DELIVERY("ON_DELIVERY", "В доставке"),
        DELIVERED("DELIVERED", "Доставлен"),
        PARTIALLY_DELIVERED("PARTIALLY_DELIVERED", "Част. доставлен"),
        DELIVERED_WITHOUT_ASSEMBLY("DELIVERED_WITHOUT_ASSEMBLY", "Доставлен без сборки"),
        CANCELLED_BY_CUSTOMER_ON_DELIVERY("CANCELLED_BY_CUSTOMER_ON_DELIVERY", "Отказ при доставке"),
        TRANSFORMED("", "Преобразован"),
        DELETED("DELETED", ""),
        CANCELLED("CANCELLED", "Отменен"),
        CANCELLATION_IN_PROGRESS("CANCELLATION_IN_PROGRESS", "Отменен"),
        DRAFT("DRAFT", "Черновик"),

        // Transfer
        TRANSFER_NEW("NEW", ""),
        TRANSFER_CONFIRMED("CONFIRMED", "Отправлен");

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
        FOR_CLIENT_TO_SHOP_ROOM("DEPARTMENT", "Для клиента в торг. зал"),
        PICKUP("PICKUP", "Самовывоз"),
        DELIVERY("HOME_DELIVERY", "Доставка");

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
