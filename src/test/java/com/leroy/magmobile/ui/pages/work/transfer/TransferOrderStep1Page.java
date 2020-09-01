package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.android.AndroidScrollViewV2;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.elements.MagMobWhiteSubmitButton;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.magmobile.ui.pages.work.transfer.modal.TransferActionWithProductCardModal;
import com.leroy.magmobile.ui.pages.work.transfer.modal.TransferExitWarningModal;
import com.leroy.magmobile.ui.pages.work.transfer.widget.TransferTaskProductWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.util.List;

public class TransferOrderStep1Page extends TransferOrderPage {

    AndroidScrollViewV2<TransferTaskProductWidget, TransferProductData> productScrollView =
            new AndroidScrollViewV2<>(driver,
                    AndroidScrollView.TYPICAL_LOCATOR,
                    ".//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]]]",
                    TransferTaskProductWidget.class);

    @AppFindBy(followingTextAfter = "Итого: ", metaName = "Стоимость итого")
    Element totalPrice;

    @AppFindBy(text = "ТОВАРЫ СО СКЛАДА", metaName = "Кнопка 'Товары со склада'")
    MagMobWhiteSubmitButton addProductFromStockBtn;

    @AppFindBy(text = "ТОВАР")
    private MagMobWhiteSubmitButton addProductBtn;

    @AppFindBy(text = "ДАЛЕЕ")
    private MagMobGreenSubmitButton nextBtn;

    // Grab data

    @Step("Получить информацию о {index} товаре")
    public TransferProductData getTransferProductData(int index) {
        index--;
        return productScrollView.getDataObj(index);
    }

    @Step("Получить информацию о товарах в заявке")
    public List<TransferProductData> getTransferProductDataList() {
        return productScrollView.getFullDataList();
    }

    // Actions

    @Step("Нажать кнопку 'Назад'")
    public TransferExitWarningModal clickBackButton() {
        backBtn.click();
        return new TransferExitWarningModal();
    }

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

    // Verifications

    @Step("Проверить, что отображается первый шаг оформления заявки и отсутствуют добавленные товары")
    public TransferOrderStep1Page verifyElementsWhenEmpty() {
        String ps = getPageSource();
        softAssert.isTrue(getOrderNumber().isEmpty(), "Номер заявки должен отсутствовать");
        softAssert.isTrue(E("Пока пусто").isVisible(ps), "Должна отображаться надпись 'Пока пусто'");
        softAssert.isElementVisible(addProductFromStockBtn, ps);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что отображается первый шаг оформления заявки, когда добавлен(ы) товар(ы)")
    public TransferOrderStep1Page verifyElementsWhenProductsAdded() {
        String ps = getPageSource();
        softAssert.isFalse(getOrderNumber().isEmpty(), "Номер заявки должен быть");
        softAssert.isFalse(E("Пока пусто").isVisible(ps), "Видна надпись 'Пока пусто'");
        softAssert.isElementNotVisible(addProductFromStockBtn, ps);
        softAssert.isElementVisible(addProductBtn, ps);
        softAssert.isElementVisible(nextBtn, ps);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что {index}-ый добавленный товар соответствует ожидаемому")
    public TransferOrderStep1Page shouldTransferProductIs(int index, TransferProductData transferProductData) {
        index--;
        TransferProductData expectedTransferProductData = transferProductData.clone();
        expectedTransferProductData.setTotalStock(null);
        productScrollView.getDataObj(index).assertEqualsNotNullExpectedFields(expectedTransferProductData);
        return this;
    }

    @Step("Проверить, что итого стоимость равна {expectedTotalPrice}")
    public TransferOrderStep1Page shouldTotalPriceIs(double expectedTotalPrice) {
        anAssert.isEquals(ParserUtil.strToDouble(totalPrice.getText()), expectedTotalPrice,
                "Неверная стоимость 'итого'");
        return this;
    }

}
