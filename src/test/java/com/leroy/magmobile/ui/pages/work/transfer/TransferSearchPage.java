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

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Товары на отзыв']]//android.view.ViewGroup/android.widget.TextView",
            metaName = "Счетчик товаров на панели 'Товары на отзыв'")
    Element transferProductCount;

    @Override
    protected void waitForPageIsLoaded() {
        anAssert.isElementVisible(transferProductSearchArea, timeout);
        waitUntilProgressBarIsInvisible();
    }

    // Grab data

    @Step("Получить информацию о {index} товаре")
    public TransferProductData getTransferProduct(int index) throws Exception {
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

    @Step("Изменить кол-во у {index}-ого товара на значение {value}")
    public TransferSearchPage editProductQuantityForProduct(int index, int value) throws Exception {
        index--;
        TransferSearchProductCardWidget widget = productCardsScrollView.getWidget(index);
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

    // Verifications

    @Step("Проверить, что у {index} товара выбрано количество {value}")
    public TransferSearchPage shouldProductQuantityIs(int index, int value) throws Exception {
        index--;
        TransferProductData transferProductData = productCardsScrollView.getDataObj(index);
        anAssert.isEquals(transferProductData.getOrderedQuantity(), value,
                String.format("У %s товара неверное выбранное количество", index + 1));
        return this;
    }

    @Step("Проверить, что счетчик товаров на панели 'Товары на отзыв' = {value}")
    public TransferSearchPage shouldProductCountOnPanelIs(int value) {
        anAssert.isEquals(transferProductCount.getText(), String.valueOf(value),
                "Неверное количество товаров на панели 'Товары на отзыв'");
        return this;
    }

}
