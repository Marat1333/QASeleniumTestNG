package com.leroy.constants;

public class SalesDocumentsConst {

    // Состояния документа
    public enum States {
        CREATED("", "Создан"),
        AUTO_PROCESSING("","Автообработка"),
        TRANSFORMED("","Преобразован"),
        CANCELLED("CANCELLED", "Отменен"),
        DRAFT("DRAFT","Черновик");

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
    public static final String ESTIMATE_TYPE = "Смета";
    public static final String BASKET_TYPE = "Корзина";
}
