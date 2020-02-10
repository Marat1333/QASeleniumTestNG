package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;

public class PricesAndQuantityPage extends BaseAppPage {

    public PricesAndQuantityPage(TestContext context) {
        super(context);
    }

    @AppFindBy(text = "ПОСТАВКИ")
    MagMobGreenSubmitButton supplyBtn;

    public void shouldNotSupplyBtnBeDisplayed() {
        anAssert.isFalse(supplyBtn.isVisible(), "Раздел \"Поставки\" не отображен");
    }
}
