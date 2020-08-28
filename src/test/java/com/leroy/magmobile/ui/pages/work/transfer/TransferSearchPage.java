package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.android.AndroidScrollViewV2;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.magmobile.ui.pages.work.transfer.widget.TransferSearchProductCardWidget;
import io.qameta.allure.Step;

public class TransferSearchPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "TransferProductsSearchMainScreen", metaName = "Область 'Поиск товаров на складе'")
    Element transferProductSearchArea;

    private AndroidScrollViewV2<TransferSearchProductCardWidget, TransferProductData>
            productCardsScrollView = new AndroidScrollViewV2<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]]]",
            TransferSearchProductCardWidget.class);

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Товары на отзыв']]",
            metaName = "Панелька 'Товары на отзыв'")
    Element transferProductPanel;

    @Override
    protected void waitForPageIsLoaded() {
        anAssert.isElementVisible(transferProductSearchArea, timeout);
    }

    // Grab data

    @Step("Получить информацию о {index} товаре")
    public TransferProductData getTransferProduct(int index) {
        index--;
        return productCardsScrollView.getDataObj(index);
    }

    // Actions

    @Step("Нажать на {index}-ую карточку товара")
    public AddProduct35Page<TransferSearchPage> clickProductCard(int index) throws Exception {
        index--;
        productCardsScrollView.clickElemByIndex(index);
        return new AddProduct35Page<>(TransferSearchPage.class);
    }

    @Step("Изменить кол-во у 1-ого товара на значение {value}")
    public TransferSearchPage editProductQuantityForFirstProduct(int value) throws Exception {
        TransferSearchProductCardWidget widget = productCardsScrollView.getFirstWidget();
        widget.editProductQuantity(value);
        waitUntilProgressBarAppearsAndDisappear();
        shouldNotAnyErrorVisible();
        return this;
    }

    @Step("Нажать на панель 'Товары на отзыв'")
    public TransferOrderStep1Page clickTransferProductPanel() {
        anAssert.isElementVisible(transferProductPanel, timeout);
        transferProductPanel.click();
        return new TransferOrderStep1Page();
    }
}
