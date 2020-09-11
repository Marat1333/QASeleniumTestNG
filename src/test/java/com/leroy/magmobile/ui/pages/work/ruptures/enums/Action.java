package com.leroy.magmobile.ui.pages.work.ruptures.enums;

public enum Action {
    GIVE_APOLOGISE(0, "Поставить извиняшку"),
    CALL_SUPPLIER(1, "Позвонить поставщику"),
    ORDER_FROM_SUPPLIER(2, "Заказать у поставщика"),
    REMOVE_PRICE_TAG(3, "Убрать ценник"),
    MAKE_C3_CORRECTION(4, "Сделать коррекцию C3"),
    RECALL_FROM_RM(5, "Отозвать с RM"),
    FIND_PRODUCT_AND_LAY_IT_OUT(6, "Найти товар и выложить"),
    STICK_RED_STICKER(7, "Наклеить красный стикер"),
    ALL_ACTIONS(8, "Все задачи");


    private int actionNumber;
    private String actionName;

    Action(int actionNumber, String actionName) {
        this.actionNumber = actionNumber;
        this.actionName = actionName;
    }

    public int getActionNumber() {
        return actionNumber;
    }

    public String getActionName() {
        return actionName;
    }
}
