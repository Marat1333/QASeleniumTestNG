package com.leroy.magportal.ui.pages.picking.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;

public abstract class SplitPickingModal extends MagPortalBasePage {

    protected final static String MODAL_DIV_XPATH = "//div[contains(@class, 'Modal-content-container lm-puz2-Picking-SplitModal')]";

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//p")
    Element header;

    @Override
    protected void waitForPageIsLoaded() {
        E(MODAL_DIV_XPATH).waitForVisibility();
    }
}
