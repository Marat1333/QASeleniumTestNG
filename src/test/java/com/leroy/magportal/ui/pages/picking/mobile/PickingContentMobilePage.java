package com.leroy.magportal.ui.pages.picking.mobile;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import io.qameta.allure.Step;

public class PickingContentMobilePage extends MagPortalBasePage {

    private static final String DOCUMENT_NUMBER_XPATH = "//div[contains(@class, 'Picking-PickingViewHeader')]//button/following-sibling::div/div";

    @WebFindBy(xpath = DOCUMENT_NUMBER_XPATH + "/span")
    Element buildNumber;

    @WebFindBy(xpath = DOCUMENT_NUMBER_XPATH + "div/span")
    Element partOrderNumber;

    @WebFindBy(xpath = DOCUMENT_NUMBER_XPATH + "/span[2]")
    Element assemblyType;

    @WebFindBy(xpath = "//button[contains(@class, 'Picking-InfoCard__action-button')]",
            metaName = "Кнопка 'Разместить'")
    Element placeBtn;

    // Actions

    @Step("Нажать кнопку 'Разместить'")
    public PickingPlaceOrderMobileModal clickPlaceButton() {
        placeBtn.click();
        return new PickingPlaceOrderMobileModal();
    }

}
