package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class SuccessReviewSendingPage extends CommonMagMobilePage {
    @AppFindBy(text = "Спасибо за твой отзыв!")
    Element thankULbl;

    @AppFindBy(text = "ВЕРНУТЬСЯ К ТОВАРУ")
    Button backToProduct;

    @Override
    public void waitForPageIsLoaded() {
        thankULbl.waitForVisibility();
    }

    @Step("Вернуться к товару")
    public ReviewsPage backToProduct() {
        backToProduct.click();
        return new ReviewsPage();
    }

    public SuccessReviewSendingPage verifyRequiredElements() {
        softAssert.areElementsVisible(thankULbl, backToProduct);
        softAssert.verifyAll();
        return this;
    }
}
