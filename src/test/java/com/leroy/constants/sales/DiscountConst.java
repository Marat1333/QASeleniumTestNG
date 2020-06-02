package com.leroy.constants.sales;

public class DiscountConst {

    public static final String TYPE_NEW_PRICE = "NEW_PRICE";

    public enum Reasons {
        DEFECT(1, "Товар с браком"),
        AFTER_REPAIR(2, "Товар после ремонта в сервисном центре"),
        BALANCE_MEASURED_PRODUCT(3, "Остаток мерного товара"),
        INCOMPLETE_KIT(4, "Неполный комплект"),
        PRODUCT_SAMPLE(5, "Образец товара");

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        Reasons(int id, String name) {
            this.id = id;
            this.name = name;
        }

    }


}
