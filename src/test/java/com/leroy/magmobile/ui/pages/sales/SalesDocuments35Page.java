package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;

public class SalesDocuments35Page extends SalesDocumentsPage {

    public SalesDocuments35Page(TestContext context) {
        super(context);
    }

    @AppFindBy(text = "ОФОРМИТЬ ПРОДАЖУ", metaName = "Кнопка 'ОФОРМИТЬ ПРОДАЖУ'")
    private MagMobSubmitButton submitBtn;

    @Override
    public MagMobSubmitButton getSubmitBtn() {
        return this.submitBtn;
    }
}
