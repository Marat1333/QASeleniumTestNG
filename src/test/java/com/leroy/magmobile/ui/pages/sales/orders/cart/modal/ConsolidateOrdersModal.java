package com.leroy.magmobile.ui.pages.sales.orders.cart.modal;

import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.modal.CommonConfirmModal;

public class ConsolidateOrdersModal extends CommonConfirmModal {

    @Override
    protected Element confirmBtn() {
        return E("ОБЪЕДИНИТЬ");
    }

    @Override
    protected Element cancelBtn() {
        return E("ОТМЕНА");
    }
}
