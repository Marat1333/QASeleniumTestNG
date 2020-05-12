package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.elements.MagMobWhiteSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.models.sales.SalesOrderCardData;
import com.leroy.magmobile.ui.models.sales.SalesOrderData;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.apache.commons.math3.util.Precision;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Basket35Page extends CommonMagMobilePage {

    public final static String SCREEN_TITLE = "Корзина";

    public Basket35Page(Context context) {
        super(context);
    }

    public static class PageState {
        boolean productIsAdded;
        Boolean manyOrders = false;

        public boolean isProductIsAdded() {
            return productIsAdded;
        }

        public PageState setProductIsAdded(boolean productIsAdded) {
            this.productIsAdded = productIsAdded;
            return this;
        }

        public Boolean isManyOrders() {
            return manyOrders;
        }

        public PageState setManyOrders(Boolean manyOrders) {
            this.manyOrders = manyOrders;
            return this;
        }
    }

    @AppFindBy(accessibilityId = "BackButton",
            metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='DefaultScreenHeader']/android.widget.TextView[1]")
    protected Element screenTitle;

    @AppFindBy(text = "Пока пусто")
    Element emptyInfoMessageLbl;

    AndroidScrollView<SalesOrderCardData> orderCardsScrollView = new AndroidScrollView<>(
            driver, AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]",
            OrderRowProductWidget.class
    );

    @AppFindBy(xpath = "//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]")
    private OrderRowProductWidget singleProductCard;

    // Bottom Area
    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/preceding-sibling::android.widget.TextView",
            metaName = "Текст с количеством и весом товара")
    Element countAndWeightProductLbl;

    @AppFindBy(text = "Итого: ")
    Element totalPriceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/following-sibling::android.widget.TextView")
    Element totalPriceVal;

    @AppFindBy(text = "ТОВАР")
    private MagMobWhiteSubmitButton addProductBtn;

    @AppFindBy(text = "ОФОРМИТЬ")
    private MagMobGreenSubmitButton makeSalesBtn;

    @AppFindBy(text = "ТОВАРЫ И УСЛУГИ", metaName = "Кнопка 'Товары и Услуги'")
    MagMobWhiteSubmitButton productAndServiceBtn;

    @Override
    public void waitForPageIsLoaded() {
        screenTitle.waitUntilTextIsEqualTo(SCREEN_TITLE);
        waitUntilProgressBarIsInvisible();
    }

    public static boolean isThisPage(TestContext context) {
        String ps = getPageSource(context.getDriver());
        Element el = new Element(context.getDriver(),
                By.xpath("//*[contains(@content-desc, 'Screen')]//android.widget.TextView"));
        return el.isVisible(ps) && el.getText(ps).equals(Basket35Page.SCREEN_TITLE);
    }

    // --------- GRAB DATA ----------------------- //

    @Step("Получить из корзины информацию о {index} товаре/услуге")
    public SalesOrderCardData getSalesOrderCardDataByIndex(int index) {
        index--;
        return orderCardsScrollView.getDataObj(index);
    }

    @Step("Получить из корзины информацию о всех добавленных товарах/услугах")
    public List<SalesOrderCardData> getSalesOrderCardDataList() {
        return orderCardsScrollView.getFullDataList();
    }

    @Step("Посчитать кол-во товаров/услуг в корзине")
    public int getCountOfOrderCards() {
        return orderCardsScrollView.getRowCount();
    }

    // -------------- ACTIONS ---------------------------//

    @Step("Нажмите на {index}-ую карточку товара/услуги")
    public CartActionWithProductCardModalPage clickCardByIndex(int index) throws Exception {
        index--;
        orderCardsScrollView.clickElemByIndex(index);
        return new CartActionWithProductCardModalPage(context);
    }

    @Step("Нажмите ОФОРМИТЬ")
    public ProcessOrder35Page clickMakeSalesButton() {
        makeSalesBtn.click();
        return new ProcessOrder35Page(context);
    }

    @Step("Нажмите кнопку для добавления товара в корзину")
    public SearchProductPage clickAddProductButton() {
        if (addProductBtn.isVisible())
            addProductBtn.click();
        else
            productAndServiceBtn.click();
        return new SearchProductPage(context);
    }

    // ------------- Verifications ----------------------//

    @Step("Проверить, что страница 'Корзина' отображается корректно")
    public Basket35Page verifyRequiredElements(PageState state) {
        String ps = getPageSource();
        // Всегда есть эти элементы:
        softAssert.areElementsVisible(ps, backBtn, screenTitle);
        // Разные состояния:
        List<Element> expectedElements = new ArrayList<>();
        if (state.isProductIsAdded())
            expectedElements.addAll(Arrays.asList(singleProductCard, totalPriceVal,
                    singleProductCard, totalPriceLbl, totalPriceVal, countAndWeightProductLbl));
        else
            expectedElements.addAll(Arrays.asList(productAndServiceBtn, emptyInfoMessageLbl));
        if (state.isManyOrders() != null) {
            if (state.isManyOrders() || !state.isProductIsAdded())
                expectedElements.add(productAndServiceBtn);
            else
                expectedElements.addAll(Arrays.asList(addProductBtn, makeSalesBtn));
        }
        softAssert.areElementsVisible(ps, expectedElements.toArray(new Element[0]));
        softAssert.isElementTextEqual(screenTitle, SCREEN_TITLE, ps);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что общее кол-во карточек товаров/услуг в корзине = {count}")
    public Basket35Page shouldCountOfCardsIs(int count) {
        anAssert.isEquals(getCountOfOrderCards(), count,
                "Неверное кол-во товаров на странице");
        return this;
    }

    @Step("Проверить, что карточка продукта/услуги с текстом '{text}' содержит следующие данные: {expectedOrderCardData}")
    public Basket35Page shouldOrderCardDataWithTextIs(String text, SalesOrderCardData expectedOrderCardData) {
        // Поля, которые мы не можем проверить, убираем из проверки:
        expectedOrderCardData.getProductCardData().setBarCode(null);
        expectedOrderCardData.getProductCardData().setAvailableQuantity(null);

        CardWidget<SalesOrderCardData> widget = orderCardsScrollView.searchForWidgetByText(text);
        anAssert.isNotNull(widget, String.format("Не найдена карточка содержащая текст %s", text),
                String.format("Карточка с текстом %s должна быть", text));
        SalesOrderCardData actualOrderCardData = widget.collectDataFromPage();
        anAssert.isTrue(actualOrderCardData.compareOnlyNotNullFields(expectedOrderCardData),
                "Неправильная карточка, содержащая текст '" + text + "'. " +
                        "Актуальное значение: " + actualOrderCardData.toString(),
                "Ожидалось: " + expectedOrderCardData.toString());
        return this;
    }

    @Step("Проверить, что информация о заказе в корзине должна быть следующая: {expectedOrderData}")
    public Basket35Page shouldOrderDataIs(SalesOrderData expectedOrderData) {
        List<SalesOrderCardData> actualOrderCardDataList = orderCardsScrollView.getFullDataList();
        anAssert.isEquals(actualOrderCardDataList.size(), expectedOrderData.getOrderCardDataList().size(),
                "Разное кол-во карточек товаров");
        for (int i = 0; i < actualOrderCardDataList.size(); i++) {
            SalesOrderCardData expectedOrderCardData = expectedOrderData.getOrderCardDataList().get(i);
            SalesOrderCardData actualOrderCardData = actualOrderCardDataList.get(i);

            softAssert.isTrue(actualOrderCardData.compareOnlyNotNullFields(expectedOrderCardData),
                    (i + 1) + "-ая карточка, содержит неверные данные. " +
                            "Актуальное значение: " + actualOrderCardData.toString(),
                    "Ожидалось: " + expectedOrderCardData.toString());
        }

        // Проверяем Total информацию
        String pageSource = getPageSource();
        String[] actualCountProductAndWeight = countAndWeightProductLbl.getText(pageSource).split("•");
        anAssert.isTrue(actualCountProductAndWeight.length == 2,
                "Что-то не так с меткой содержащей информацию о кол-ве и весе товара");
        String actualTotalPrice = totalPriceVal.getText(pageSource).replaceAll("\\D+", "");
        softAssert.isEquals(ParserUtil.strToInt(actualCountProductAndWeight[0]),
                expectedOrderData.getProductCount(), "Неверное кол-во товара");
        softAssert.isEquals(ParserUtil.strToDouble(actualCountProductAndWeight[1]),
                Precision.round(expectedOrderData.getTotalWeight(), 1), "Неверный вес товара");
        softAssert.isEquals(ParserUtil.strToDouble(actualTotalPrice), expectedOrderData.getTotalPrice(),
                "Неверное сумма итого");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что в корзине нет товара с ЛМ кодом {expLmCode}")
    public Basket35Page shouldProductBeNotPresentInCart(String expLmCode) {
        List<SalesOrderCardData> salesOrderCardDataList = orderCardsScrollView.getFullDataList();
        for (SalesOrderCardData orderCardData : salesOrderCardDataList) {
            anAssert.isNotEquals(orderCardData.getProductCardData().getLmCode(), expLmCode,
                    "Продукта с таким ЛМ код быть не должно");
        }
        return this;
    }

}
