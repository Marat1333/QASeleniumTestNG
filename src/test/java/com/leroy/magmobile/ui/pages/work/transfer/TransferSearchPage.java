package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.android.AndroidScrollViewV2;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.magmobile.ui.pages.work.transfer.widget.TransferSearchProductCardWidget;
import io.qameta.allure.Step;

import java.util.List;

public class TransferSearchPage extends CommonMagMobilePage {

    @AppFindBy(text = "Поиск товаров на складе", metaName = "Область 'Поиск товаров на складе'")
    Element transferProductSearchArea;

    @AppFindBy(accessibilityId = "ScreenTitle-TransferProductsSearch", metaName = "Редактируемая строка поиска")
    EditBox editSearchFld;

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
        waitForAnyOneOfElementsIsVisible(transferProductSearchArea, editSearchFld);
        waitUntilProgressBarIsInvisible();
    }

    // Grab data

    @Step("Получить информацию о {index} товаре")
    public TransferProductData getTransferProduct(int index) throws Exception {
        index--;
        return productCardsScrollView.getDataObj(index);
    }

    // Actions

    @Step("Найти товар с ЛМ код = {lmCode}")
    public TransferSearchPage searchForProductByLmCode(String lmCode) {
        transferProductSearchArea.click();
        editSearchFld.clearFillAndSubmit(lmCode);
        return this;
    }

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

    @Step("Проверить, что у {index} товара отображается предупреждение о том, что выбрано неверное количество")
    public TransferSearchPage shouldProductHasWrongQuantityTooltip(int index) throws Exception {
        index--;
        TransferSearchProductCardWidget widget = productCardsScrollView.getWidget(index);
        anAssert.isTrue(widget.isWrongQuantityErrorTooltipVisible(),
                "Предупреждение о необходимости изменить количество не отображается");
        return this;
    }

    @Step("Убедиться, что состав отзыва у {index} товара: Штучный {single}; Моно-Паллет {monoPallet}; Микс-Паллет {mixPallet}; Выбрано {ordered}")
    public TransferSearchPage shouldReviewCompositionIs(
            int index, int single, int monoPallet, int mixPallet, int ordered) throws Exception {
        index--;
        TransferProductData transferProductData = productCardsScrollView.getDataObj(index);
        softAssert.isEquals(transferProductData.getSelectedPieceQuantity(), single,
                "Неверный состав отзыва для штучного товара");
        softAssert.isEquals(transferProductData.getSelectedMonoPalletQuantity(), monoPallet,
                "Неверный состав отзыва для моно-паллета");
        softAssert.isEquals(transferProductData.getSelectedMixPalletQuantity(), mixPallet,
                "Неверный состав отзыва для микс-паллета");
        softAssert.isEquals(transferProductData.getOrderedQuantity(), ordered,
                "Неверное заказанное (выбранное) количество");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что счетчик товаров на панели 'Товары на отзыв' = {value}")
    public TransferSearchPage shouldProductCountOnPanelIs(int value) {
        anAssert.isEquals(transferProductCount.getText(), String.valueOf(value),
                "Неверное количество товаров на панели 'Товары на отзыв'");
        return this;
    }

    @Step("Проверить, что в списке отображаются только необходимы товары")
    public TransferSearchPage shouldTransferProductsAre(List<TransferProductData> expectedProductList) throws Exception {
        expectedProductList.forEach(p -> p.setPrice(null));
        List<TransferProductData> actualProducts = productCardsScrollView.getFullDataList(5);
        anAssert.isEquals(actualProducts.size(), expectedProductList.size(), "Ожидалось другое количество товаров");
        for (int i = 0; i < expectedProductList.size(); i++) {
            actualProducts.get(i).assertEqualsNotNullExpectedFields(expectedProductList.get(i));
        }
        return this;
    }

}
