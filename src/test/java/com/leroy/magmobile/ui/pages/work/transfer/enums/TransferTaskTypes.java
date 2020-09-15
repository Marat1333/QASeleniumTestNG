package com.leroy.magmobile.ui.pages.work.transfer.enums;

public enum TransferTaskTypes {
    CLIENT_IN_SHOP_ROOM("Клиенту в торговый зал"),
    OVER_SIZED_CHECKOUT("Крупногабаритная касса");

    private String value;

    TransferTaskTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
