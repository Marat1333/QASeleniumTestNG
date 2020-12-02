package com.leroy.constants.sales;

public class DiscountConst {

    public static final String TYPE_NEW_PRICE = "NEW_PRICE";

    public enum Reasons {
        DEFECT(1, "Товар с браком"),
        AFTER_REPAIR(2, "Товар после ремонта в сервисном центре"),
        BALANCE_MEASURED_PRODUCT(3, "Остаток мерного товара"),
        INCOMPLETE_KIT(4, "Не полный комплект"),
        PRODUCT_SAMPLE(5, "Образец товара"),
        CUSTOMER_EQUIVALENT_REASON(6, "Аналог для клиента ИМ"),
        B2B_PRICE_ADJUSTMENT(11, "Корректировка цены B2B"),
        B2B_EXTRA_CHARGE(12, "Наценка B2B");

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
