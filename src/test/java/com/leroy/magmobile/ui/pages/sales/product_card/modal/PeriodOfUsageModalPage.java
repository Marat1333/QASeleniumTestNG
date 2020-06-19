package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.enums.ReviewOptions;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.product_card.FirstLeaveReviewPage;

public class PeriodOfUsageModalPage extends CommonMagMobilePage {
    @AppFindBy(text = "Срок использования")
    Element modalTitleLbl;

    public FirstLeaveReviewPage chosePeriodOfUsage(ReviewOptions option)throws Exception{
        modalTitleLbl.findChildElement("./following-sibling::*//*[contains(@text,'"+option.getName()+"')]/following-sibling::*").click();
        return new FirstLeaveReviewPage();
    }

    @Override
    public void waitForPageIsLoaded() {
        modalTitleLbl.waitForVisibility();
    }
}
