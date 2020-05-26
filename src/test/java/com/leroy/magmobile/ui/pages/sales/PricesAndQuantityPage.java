package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;

public class PricesAndQuantityPage extends CommonMagMobilePage {

    @AppFindBy(text = "ПОСТАВКИ")
    MagMobGreenSubmitButton supplyBtn;

    public void shouldNotSupplyBtnBeDisplayed() {
        anAssert.isFalse(supplyBtn.isVisible(), "Раздел \"Поставки\" не отображен");
    }
}
