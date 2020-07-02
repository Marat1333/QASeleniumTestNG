package com.leroy.magportal.ui.pages.cart_estimate.modal;

import com.leroy.core.ContextProvider;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import org.openqa.selenium.By;

public class ExtendedSearchModal extends MagPortalBasePage {

    private static final String MODAL_DIV_XPATH = "//div[contains(@class, 'ExtendedSearchViewModal')]";

    public static boolean isModalVisible() {
        return new Element(ContextProvider.getDriver(), By.xpath(MODAL_DIV_XPATH)).isVisible();
    }
}
