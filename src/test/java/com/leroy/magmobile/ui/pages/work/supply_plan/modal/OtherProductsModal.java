package com.leroy.magmobile.ui.pages.work.supply_plan.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.work.modal.CommonHintModalPage;

public class OtherProductsModal extends CommonHintModalPage {
    @AppFindBy(text = "Остальное в заказе")
    Element title;

    @AppFindBy(containsText = "По оставшимся артикулам пока нет записей поставщика.")
    Element description;

    @Override
    public OtherProductsModal verifyRequiredElements() {
        softAssert.areElementsVisible(title, description);
        softAssert.verifyAll();
        return this;
    }
}
