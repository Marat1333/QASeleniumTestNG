package com.leroy.magportal.ui.constants;

public class OrderConst {

    public static final class Status {

        public static final String CREATED = "Создан";
        public static final String TECH_PROCESSING = "Тех. Обработка";
        public static final String ASSEMBLY = "Сборка";
        public static final String ASSEMBLY_PAUSE = "Сборка (пауза)";
        public static final String PICKED = "Собран";
        public static final String PICKED_PARTIALLY = "Част. собран";
        public static final String ISSUED = "Выдан";
        public static final String ISSUED_PARTIALLY = "Част. выдан";
        public static final String CANCELLED = "Отменен";
    }

    public static final class DeliveryType {

        public static final String PICKUP = "Самовывоз";
        public static final String DELIVERY_TK = "Доставка ТК";
        public static final String DELIVERY_KK = "Доставка КК";
        public static final String PVZ = "ПВЗ";
    }

}
