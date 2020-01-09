package com.leroy.pages.app.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.elements.MagMobButton;

public class PricesAndQuantityPage extends BaseAppPage {

    public PricesAndQuantityPage (TestContext context){
        super(context);
    }

    @AppFindBy(text = "ПОСТАВКИ")
    MagMobButton supplyBtn;

    public void shouldNotSupplyBtnBeDisplayed(){
        anAssert.isFalse(supplyBtn.isVisible(),"Раздел \"Поставки\" не отображен");
    }
}
