package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;

public abstract class CommonActionWithProductModalPage extends CommonMagMobilePage {

    @AppFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"Button\"])[1]/android.view.ViewGroup",
            metaName = "Кнопка для закрытия модального окна")
    Element closeBtn;

    @AppFindBy(text = "Действия с товаром")
    Element headerLbl;

    @AppFindBy(text = "Уведомить клиента о наличии")
    Element notifyClientBtn;

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
        waitUntilProgressBarIsInvisible();
    }

    // Verifications

    public abstract CommonActionWithProductModalPage verifyRequiredElements(boolean isAvsProduct);

}
