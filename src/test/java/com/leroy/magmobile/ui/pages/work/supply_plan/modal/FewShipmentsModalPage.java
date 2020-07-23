package com.leroy.magmobile.ui.pages.work.supply_plan.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.work.modal.CommonHintModalPage;

public class FewShipmentsModalPage extends CommonHintModalPage {
    @AppFindBy(text = "Заказ состоит из нескольких отгрузок")
    Element title;

    @AppFindBy(containsText = "Здесь отображена информация о составе и статусе по каждой отгрузке из заказа.")
    Element description;

    @Override
    public FewShipmentsModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(title, description);
        softAssert.verifyAll();
        return this;
    }
}
