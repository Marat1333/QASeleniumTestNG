package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public abstract class CommonActionWithProductModalPage extends CommonMagMobilePage {

    @AppFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"Button\"])[1]/android.view.ViewGroup",
            metaName = "Кнопка для закрытия модального окна")
    Element closeBtn;

    @AppFindBy(text = "Действия с товаром")
    Element headerLbl;

    @AppFindBy(text = "Уведомить клиента о наличии")
    Element notifyClientBtn;

    @AppFindBy(text = "Напечатать ценник")
    Button printTagBtn;

    @Step("Напечатать ценник")
    public void printTag() {
        printTagBtn.click();
    }

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
        waitUntilProgressBarIsInvisible();
    }

    // Verifications

    public abstract CommonActionWithProductModalPage verifyRequiredElements(boolean isAvsProduct);

}
