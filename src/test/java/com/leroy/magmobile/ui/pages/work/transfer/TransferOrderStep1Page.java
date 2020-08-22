package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.android.AndroidScrollViewV2;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.elements.MagMobWhiteSubmitButton;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.magmobile.ui.pages.work.transfer.modal.TransferActionWithProductCardModal;
import com.leroy.magmobile.ui.pages.work.transfer.widget.TransferTaskProductWidget;
import io.qameta.allure.Step;

public class TransferOrderStep1Page extends TransferOrderPage {

    AndroidScrollViewV2<TransferTaskProductWidget, TransferProductData> productScrollView =
            new AndroidScrollViewV2<>(driver,
                    AndroidScrollView.TYPICAL_LOCATOR,
                    ".//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]]]",
                    TransferTaskProductWidget.class);

    @AppFindBy(text = "ТОВАРЫ СО СКЛАДА", metaName = "Кнопка 'Товары со склада'")
    MagMobWhiteSubmitButton addProductFromStockBtn;

    @AppFindBy(text = "ДАЛЕЕ")
    private MagMobGreenSubmitButton nextBtn;

    // Actions

    @Step("Нажать на {index} карточку товара")
    public TransferActionWithProductCardModal clickProductCard(int index) throws Exception {
        index--;
        productScrollView.clickElemByIndex(index);
        return new TransferActionWithProductCardModal();
    }

    @Step("Нажать кнопку '+ Товары со склада'")
    public TransferSearchPage clickAddProductFromStockButton() {
        addProductFromStockBtn.click();
        return new TransferSearchPage();
    }

    @Step("Нажать кнопку 'Далее ->'")
    public void clickNextButton() {
        nextBtn.click();
    }

}
