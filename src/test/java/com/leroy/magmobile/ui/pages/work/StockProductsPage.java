package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.models.work.WithdrawalProductCardData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.widgets.ProductCardWidget;
import com.leroy.magmobile.ui.pages.widgets.SelectedCardWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class StockProductsPage extends CommonMagMobilePage {

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

    @Step("Получить информацию о продукте с экрана поиска товаров на складе")
    public WithdrawalProductCardData getProductInfoByIndex(int index) throws Exception {
        WithdrawalProductCardData cardData = new WithdrawalProductCardData();
        int count = pieceProductCards.getCount();
        if (index >= count)
            throw new IndexOutOfBoundsException("На странице " + count +
                    " штучных товаров. Тест пытался выбрать " + (index + 1));
        ProductCardWidget cardObj = pieceProductCards.get(index);
        cardData.setLmCode(cardObj.getNumber());
        cardData.setTitle(cardObj.getName());
        cardData.setPriceUnit(cardObj.getQuantityUnit());
        cardData.setAvailableQuantity(ParserUtil.strToDouble(cardObj.getQuantity()));
        return cardData;
    }

    @Step("Забрать со страницы информацию о {index} товаре")
    public WithdrawalProductCardData getSelectedProductInfoByIndex(int index) throws Exception {
        index--;
        SelectedCardWidget cardWidget = selectedProductCards.get(index);
        WithdrawalProductCardData cardData = new WithdrawalProductCardData();
        cardData.setLmCode(cardWidget.getNumber());
        cardData.setTitle(cardWidget.getName());
        cardData.setPriceUnit(cardWidget.getQuantityUnit());
        cardData.setAvailableQuantity(ParserUtil.strToDouble(cardWidget.getQuantity()));
        cardData.setSelectedQuantity(ParserUtil.strToDouble(cardWidget.getSelectedQuantity()));
        return cardData;
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Выбрать первый товар, который поштучно хранится на складе")
    public StockProductCardPage clickFirstPieceProduct() throws Exception {
        pieceProductCards.get(0).click();
        StockProductCardPage productCardPage = new StockProductCardPage();
        productCardPage.waitForPageIsLoaded();
        return productCardPage;
    }

    @Step("Нажать кнопку ДАЛЕЕ К ПАРАМЕТРАМ ЗАЯВКИ")
    public OrderPage clickSubmitBtn() {
        submitBtn.click();
        return new OrderPage();
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
            int index, WithdrawalProductCardData expectedOrderCardData) throws Exception {
        WithdrawalProductCardData selectedProductData = getSelectedProductInfoByIndex(index);
        selectedProductData.assertEqualsNotNullExpectedFields(index - 1, expectedOrderCardData);
        return this;
    }

}
