package com.leroy.magmobile.ui.pages.sales.orders.order.modal;

import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.modal.CommonConfirmModal;

public class ConfirmExitOrderModal extends CommonConfirmModal {

    @Override
    protected Element confirmBtn() {
        return E("ВЫЙТИ");
    }

    @Override
    protected Element cancelBtn() {
        return E("ОТМЕНА");
    }
}
