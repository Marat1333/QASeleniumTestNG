package com.leroy.magmobile.ui.pages.work.supply_plan.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.work.modal.CommonHintModalPage;

public class ReserveModalPage extends CommonHintModalPage {

    @AppFindBy(text = "Время зафиксировано под поставщика")
    Element title;

    @AppFindBy(containsText = "Это время забронировано под постоянного поставщика.")
    Element description;

    public ReserveModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(title, description);
        softAssert.verifyAll();
        return this;
    }
}
