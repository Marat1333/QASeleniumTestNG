package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.widgets.ProductCardWidget;
import com.leroy.magmobile.ui.pages.widgets.SelectedCardWidget;
import com.leroy.models.ProductCardData;
import com.leroy.models.WithdrawalOrderCardData;
import com.leroy.utils.Converter;
import io.qameta.allure.Step;

public class StockProductsPage extends CommonMagMobilePage {

    public StockProductsPage(Context context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "ScreenTitle")
    private Element searchArea;

    @AppFindBy(xpath = "//android.widget.ScrollView//android.view.ViewGroup//android.widget.ImageView")
    private ElementList<Element> productImages;

    @AppFindBy(xpath = "//android.widget.ScrollView//android.view.ViewGroup[@index='0' and android.widget.TextView[contains(@text, 'шт.')]]",
            metaName = "Карточки 'штучных' товаров", clazz = ProductCardWidget.class)
    private ElementList<ProductCardWidget> pieceProductCards;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='ВЫБРАННЫЕ ТОВАРЫ']]/following-sibling::android.view.ViewGroup[descendant::android.widget.ImageView]",
            clazz = SelectedCardWidget.class, metaName = "Карточки выбранных товаров")
    private ElementList<SelectedCardWidget> selectedProductCards;

    @AppFindBy(xpath = "//android.widget.TextView[@text='ВЫБРАННЫЕ ТОВАРЫ']",
            metaName = "Метка 'ВЫБРАННЫЕ ТОВАРЫ'")
    public Element selectedProductsLabel;

    @AppFindBy(xpath = "//android.widget.TextView[@text='ВСЕ ТОВАРЫ НА СКЛАДЕ']")
    private Element allProductsOnStockLabel;

    @AppFindBy(xpath = "(//android.widget.HorizontalScrollView/following::android.view.ViewGroup/android.widget.TextView)[1]",
            metaName = "Метка кол-ва товаров в корзине")
    private Element basketProductCountLabel;

    @AppFindBy(text = "ДАЛЕЕ К ПАРАМЕТРАМ ЗАЯВКИ", metaName = "Кнопка 'Далее к параметрам заявки'")
    public MagMobGreenSubmitButton submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        allProductsOnStockLabel.waitForVisibility(timeout);
    }

    // -------------------- GRAB Info from Page ----------------------------//
    public ProductCardData getPieceProductInfoByIndex(int index) throws Exception {
        ProductCardData cardData = new ProductCardData();
        int count = pieceProductCards.getCount();
        if (index >= count)
            throw new IndexOutOfBoundsException("На странице " + count +
                    " штучных товаров. Тест пытался выбрать " + (index + 1));
        ProductCardWidget cardObj = pieceProductCards.get(index);
        cardData.setLmCode(cardObj.getNumber());
        cardData.setName(cardObj.getName());
        cardData.setPriceUnit(cardObj.getQuantityUnit());
        cardData.setAvailableQuantity(Converter.strToDouble(cardObj.getQuantity()));
        return cardData;
    }

    @Step("Забрать со страницы информацию о {index} товаре")
    public WithdrawalOrderCardData getSelectedProductInfoByIndex(int index) throws Exception {
        index--;
        ProductCardData cardData = new ProductCardData();
        SelectedCardWidget cardObj = selectedProductCards.get(index);
        cardData.setLmCode(cardObj.getNumber());
        cardData.setName(cardObj.getName());
        cardData.setPriceUnit(cardObj.getQuantityUnit());
        cardData.setAvailableQuantity(Converter.strToDouble(cardObj.getQuantity()));

        WithdrawalOrderCardData withdrawalOrderCardData = new WithdrawalOrderCardData();
        withdrawalOrderCardData.setProductCardData(cardData);
        withdrawalOrderCardData.setSelectedQuantity(Converter.strToDouble(cardObj.getSelectedQuantity()));
        return withdrawalOrderCardData;
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Выбрать первый товар, который поштучно хранится на складе")
    public StockProductCardPage clickFirstPieceProduct() throws Exception {
        pieceProductCards.get(0).click();
        StockProductCardPage productCardPage = new StockProductCardPage(context);
        productCardPage.waitForPageIsLoaded();
        return productCardPage;
    }

    @Step("Нажать кнопку ДАЛЕЕ К ПАРАМЕТРАМ ЗАЯВКИ")
    public OrderPage clickSubmitBtn() {
        submitBtn.click();
        return new OrderPage(context);
    }

    /* ------------------------- Verifications -------------------------- */

    @Step("Проверить, что на экране отображаются товары")
    public StockProductsPage shouldAnyProductAvailableOnPage() {
        anAssert.isTrue(productImages.getCount() > 0,
                "На странице должен быть хотя бы один доступный товар");
        return this;
    }

    @Step("Проверить, что страница отображается корректно")
    public StockProductsPage verifyRequiredElements() {
        softAssert.areElementsVisible(searchArea,
                selectedProductsLabel, submitBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбрано {num} товаров")
    public StockProductsPage shouldCountOfSelectedProductsIs(int num) {
        softAssert.isEquals(selectedProductCards.getCount(), num,
                "Должен быть %s товар в секции ВЫБРАННЫЕ ТОВАРЫ");
        softAssert.isEquals(Integer.parseInt(basketProductCountLabel.getText()), num,
                "Внизу экрана есть иконка корзинки с цифрой %s");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный продукт имеет следующие параметры: {expectedOrderCardData}")
    public StockProductsPage shouldSelectedProductIs(
            int index, WithdrawalOrderCardData expectedOrderCardData) throws Exception {
        WithdrawalOrderCardData selectedProductDataAfter = getSelectedProductInfoByIndex(index);
        anAssert.isTrue(selectedProductDataAfter.compareOnlyNotNullFields(expectedOrderCardData),
                "Неверная карточка выбранного товара. Актуальное зн.: " + selectedProductDataAfter.toString(),
                expectedOrderCardData.toString());
        return this;
    }

}
