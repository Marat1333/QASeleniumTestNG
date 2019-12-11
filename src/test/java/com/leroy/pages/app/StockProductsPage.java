package com.leroy.pages.app;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.models.ProductCardData;
import com.leroy.pages.app.widgets.ProductCardWidget;
import com.leroy.pages.app.widgets.SelectedCardWidget;
import org.openqa.selenium.WebDriver;

public class StockProductsPage extends BaseAppPage {

    public StockProductsPage(WebDriver driver) {
        super(driver);
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

    @AppFindBy(text = "ДАЛЕЕ К ПАРАМЕТРАМ ЗАЯВКИ", metaName = "Текст кнопки 'Далее к параметрам заявки'")
    public Element submitBtnLabel;

    @Override
    public void waitForPageIsLoaded() {
        allProductsOnStockLabel.waitForVisibility(timeout);
    }

    public boolean isAnyProductAvailableOnPage() {
        return productImages.getCount() > 0;
    }

    public ProductCardPage clickFirstPieceProduct() throws Exception {
        pieceProductCards.get(0).click();
        ProductCardPage productCardPage = new ProductCardPage(driver);
        productCardPage.waitForPageIsLoaded();
        return productCardPage;
    }

    public ProductCardData getPieceProductInfoByIndex(int index) throws Exception {
        ProductCardData cardData = new ProductCardData();
        int count = pieceProductCards.getCount();
        if (index <= count)
            throw new IndexOutOfBoundsException("На странице " + count +
                    " штучных товаров. Тест пытался выбрать " + (index + 1));
        ProductCardWidget cardObj = pieceProductCards.get(index);
        cardData.setNumber(cardObj.getNumber());
        cardData.setName(cardObj.getName());
        return cardData;
    }

    public ProductCardData getSelectedProductInfoByIndex(int index) throws Exception {
        ProductCardData cardData = new ProductCardData();
        SelectedCardWidget cardObj = selectedProductCards.get(index);
        cardData.setNumber(cardObj.getNumber());
        cardData.setName(cardObj.getName());
        cardData.setSelectedQuantity(cardObj.getQuantity());
        return cardData;
    }

    public int getCountSelectedProducts() {
        return selectedProductCards.getCount();
    }

    public OrderPage clickSubmitBtn() {
        submitBtnLabel.click();
        return new OrderPage(driver);
    }

    public int getCountOfProductsInBasket() {
        return Integer.parseInt(basketProductCountLabel.getText());
    }

}
