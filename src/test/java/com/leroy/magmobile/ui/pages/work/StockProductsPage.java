package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.models.ProductCardData;
import com.leroy.magmobile.ui.pages.widgets.ProductCardWidget;
import com.leroy.magmobile.ui.pages.widgets.SelectedCardWidget;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import io.qameta.allure.Step;

public class StockProductsPage extends CommonMagMobilePage {

    public StockProductsPage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "//android.widget.ScrollView//android.view.ViewGroup//android.widget.ImageView")
    private ElementList<Element> productImages;

    @AppFindBy(xpath = "//android.widget.ScrollView//android.view.ViewGroup[@index='0' and android.widget.TextView[contains(@text, 'шт.')]]",
            metaName = "Карточки 'штучных' товаров", clazz = ProductCardWidget.class)
    private ElementList<ProductCardWidget> pieceProductCards;

    @AppFindBy(xpath = "//android.widget.ScrollView//android.view.ViewGroup[android.view.ViewGroup/android.widget.TextView[@text='ВЫБРАННЫЕ ТОВАРЫ']]//android.view.ViewGroup[@index='0' and android.widget.ImageView]",
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
    public MagMobSubmitButton submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        allProductsOnStockLabel.waitForVisibility(timeout);
    }

    public ProductCardData getPieceProductInfoByIndex(int index) throws Exception {
        ProductCardData cardData = new ProductCardData();
        int count = pieceProductCards.getCount();
        if (index >= count)
            throw new IndexOutOfBoundsException("На странице " + count +
                    " штучных товаров. Тест пытался выбрать " + (index + 1));
        ProductCardWidget cardObj = pieceProductCards.get(index);
        cardData.setLmCode(cardObj.getNumber());
        cardData.setName(cardObj.getName());
        cardData.setQuantityType(cardObj.getQuantityType());
        return cardData;
    }

    public ProductCardData getSelectedProductInfoByIndex(int index) throws Exception {
        ProductCardData cardData = new ProductCardData();
        SelectedCardWidget cardObj = selectedProductCards.get(index);
        cardData.setLmCode(cardObj.getNumber());
        cardData.setName(cardObj.getName());
        cardData.setSelectedQuantity(cardObj.getSelectedQuantity());
        return cardData;
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
    public StockProductsPage verifyVisibilityOfAllElements() {
        softAssert.isElementVisible(selectedProductsLabel);
        softAssert.isElementTextEqual(submitBtn,
                "ДАЛЕЕ К ПАРАМЕТРАМ ЗАЯВКИ");
        // to be continued
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

    @Step("Проверить, что выбранный продукт имеет следующие параметры: {productData}")
    public StockProductsPage shouldSelectedProductIs(
            int index, ProductCardData productData) throws Exception {
        ProductCardData selectedProductDataAfter = getSelectedProductInfoByIndex(index);
        softAssert.isEquals(selectedProductDataAfter.getLmCode(), productData.getLmCode(),
                "Выбранный товар должен иметь номер %s");
        softAssert.isEquals(selectedProductDataAfter.getName(), productData.getName(),
                "Выбранный товар должен иметь название %s");
        softAssert.isEquals(selectedProductDataAfter.getSelectedQuantity(),
                productData.getSelectedQuantity(),
                "Выбранный товар должен иметь кол-во на отзыв %s");
        softAssert.verifyAll();
        return this;
    }

}
