package com.leroy.magmobile.ui.pages.sales.orders.cart;

import com.leroy.core.ContextProvider;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.elements.MagMobWhiteSubmitButton;
import com.leroy.magmobile.ui.models.sales.OrderAppData;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.models.sales.SalesDocumentData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.sales.widget.BottomOrderInfoWidget;
import com.leroy.magmobile.ui.pages.sales.widget.HeaderOrderInfoWidget;
import com.leroy.magmobile.ui.pages.sales.widget.ProductOrderCardAppWidget;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Data;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cart35Page extends CommonMagMobilePage {

    public final static String SCREEN_TITLE = "Корзина";

    @Builder
    @Data
    public static class PageState {
        boolean productIsAdded;
        Boolean manyOrders;
    }

    @AppFindBy(accessibilityId = "BackButton",
            metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='DefaultScreenHeader']/android.widget.TextView[1]")
    protected Element screenTitle;

    @AppFindBy(text = "Пока пусто")
    Element emptyInfoMessageLbl;

    // Карточки товаров
    AndroidScrollView<ProductOrderCardAppData> productCardsScrollView = new AndroidScrollView<>(
            driver, AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]",
            ProductOrderCardAppWidget.class
    );

    // Верхняя шапка заказов (Заказ # из # и т.д.)
    AndroidScrollView<OrderAppData> headerOrderInfoScrollView = new AndroidScrollView<>(
            driver, AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.view.ViewGroup[@content-desc='Badge-View']]",
            HeaderOrderInfoWidget.class
    );

    // Нижняя информация о заказе (вес, Итого стоимость и т.д.)
    AndroidScrollView<OrderAppData> bottomOrderInfoScrollView = new AndroidScrollView<>(
            driver, AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.widget.TextView[contains(@text, 'Итого:')]]",
            BottomOrderInfoWidget.class
    );

    @AppFindBy(xpath = "//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]")
    private ProductOrderCardAppWidget singleProductCard;

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

    public static boolean isThisPage() {
        WebDriver driver = ContextProvider.getDriver();
        String ps = getPageSource(driver);
        Element el = new Element(driver,
                By.xpath("//*[contains(@content-desc, 'Screen')]//android.widget.TextView"));
        return el.isVisible(ps) && el.getText(ps).equals(Cart35Page.SCREEN_TITLE);
    }

    private boolean isMoreThanOneOrder(String ps) {
        return E("contains(Заказ 1 из)").isVisible(ps);
    }

    // --------- GRAB DATA ----------------------- //

    @Step("Получить цифру кол-ва товаров в корзине на нижней панели")
    public int getProductCount(String ps) {
        String[] actualCountProductAndWeight = countAndWeightProductLbl.getText(ps).split("•");
        return ParserUtil.strToInt(actualCountProductAndWeight[0]);
    }

    @Step("Получить общий вес товаров в корзине на нижней панели")
    public Double getTotalWeight(String ps) {
        String[] actualCountProductAndWeight = countAndWeightProductLbl.getText(ps).split("•");
        return ParserUtil.strToDouble(actualCountProductAndWeight[1]);
    }

    @Step("Получить общую стоимость товаров с нижней панели")
    public Double getTotalPrice(String ps) {
        return ParserUtil.strToDouble(totalPriceVal.getText(ps));
    }

    @Step("Получить информацию о документе")
    public SalesDocumentData getSalesDocumentData() {
        List<OrderAppData> actualOrderDataList = new ArrayList<>();
        String ps = getPageSource();
        SalesDocumentData salesDocumentData = new SalesDocumentData();
        salesDocumentData.setTitle(screenTitle.getText(ps));
        List<ProductOrderCardAppData> products = productCardsScrollView.getFullDataList();
        if (isMoreThanOneOrder(ps)) { // Если несколько заказов в одной корзине
            List<OrderAppData> headerOrderDataList = headerOrderInfoScrollView
                    .getFullDataList(2, true);
            List<OrderAppData> bottomOrderDataList = bottomOrderInfoScrollView.getFullDataList(
                    2, true);
            if (headerOrderDataList.size() != bottomOrderDataList.size())
                throw new RuntimeException(
                        "Тест нашел разное кол-во верхних и нижних плашек с информацией о заказе");

            int iProductCount = 0;
            for (int i = 0; i < headerOrderDataList.size(); i++) {
                OrderAppData orderAppData = headerOrderDataList.get(i);
                OrderAppData bottomOrderInfoData = bottomOrderDataList.get(i);
                orderAppData.setTotalWeight(bottomOrderInfoData.getTotalWeight());
                orderAppData.setTotalPrice(bottomOrderInfoData.getTotalPrice());
                if (orderAppData.getProductCount() != null) {
                    anAssert.isEquals(orderAppData.getProductCount(), bottomOrderInfoData.getProductCount(),
                            "Разная информация о кол-ве товаров в заказе в нижней (зеленой) и верхней плашке");
                }
                orderAppData.setProductCount(bottomOrderInfoData.getProductCount());
                orderAppData.setProductCardDataList(products.subList(iProductCount, products.size()));
                actualOrderDataList.add(orderAppData);
                iProductCount += orderAppData.getProductCount();
            }
        } else { // Если один заказ в корзине
            OrderAppData orderAppData = new OrderAppData();
            orderAppData.setTotalWeight(getTotalWeight(ps));
            orderAppData.setTotalPrice(getTotalPrice(ps));
            orderAppData.setProductCount(getProductCount(ps));
            orderAppData.setProductCardDataList(products);
            actualOrderDataList.add(orderAppData);
        }
        salesDocumentData.setOrderAppDataList(actualOrderDataList);
        return salesDocumentData;
    }

    @Step("Получить из корзины информацию о {index} товаре/услуге")
    public ProductOrderCardAppData getProductCardDataByIndex(int index) {
        index--;
        return productCardsScrollView.getDataObj(index);
    }

    @Step("Получить из корзины информацию о всех добавленных товарах/услугах")
    public List<ProductOrderCardAppData> getProductCardDataList() {
        return productCardsScrollView.getFullDataList();
    }

    @Step("Посчитать кол-во товаров/услуг в корзине")
    public int getCountOfProductCards() {
        return productCardsScrollView.getRowCount();
    }

    // -------------- ACTIONS ---------------------------//

    @Step("Нажмите на {index}-ую карточку товара/услуги")
    public CartActionWithProductCardModalPage clickCardByIndex(int index) throws Exception {
        index--;
        productCardsScrollView.clickElemByIndex(index);
        return new CartActionWithProductCardModalPage();
    }

    @Step("Нажмите ОФОРМИТЬ")
    public ProcessOrder35Page clickMakeSalesButton() {
        makeSalesBtn.click();
        return new ProcessOrder35Page();
    }

    @Step("Нажмите кнопку для добавления товара в корзину")
    public SearchProductPage clickAddProductButton() {
        if (addProductBtn.isVisible())
            addProductBtn.click();
        else
            productAndServiceBtn.click();
        return new SearchProductPage();
    }

    // ------------- Verifications ----------------------//

    @Step("Проверить, что страница 'Корзина' отображается корректно")
    public Cart35Page verifyRequiredElements(PageState state) {
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
        if (state.getManyOrders() != null) {
            if (state.getManyOrders() || !state.isProductIsAdded())
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
    public Cart35Page shouldCountOfCardsIs(int count) {
        anAssert.isEquals(getCountOfProductCards(), count,
                "Неверное кол-во товаров на странице");
        return this;
    }

    @Step("Проверить, что карточка продукта/услуги с текстом '{text}' содержит следующие данные: (expectedProductCardData)")
    public Cart35Page shouldProductCardDataWithTextIs(String text, ProductOrderCardAppData expectedProductCardData) {
        CardWidget<ProductOrderCardAppData> widget = productCardsScrollView.searchForWidgetByText(text);
        anAssert.isNotNull(widget, String.format("Не найдена карточка содержащая текст %s", text),
                String.format("Карточка с текстом %s должна быть", text));
        ProductOrderCardAppData actualProductCardData = widget.collectDataFromPage();
        if (expectedProductCardData.getAvailableTodayQuantity() != null &&
                expectedProductCardData.getAvailableTodayQuantity() > expectedProductCardData.getSelectedQuantity())
            expectedProductCardData.setAvailableTodayQuantity(null);
        actualProductCardData.assertEqualsNotNullExpectedFields(expectedProductCardData);
        return this;
    }

    @Step("Проверить, что у товара отсутствует скидка")
    public Cart35Page shouldProductDoesNotHaveDiscount(ProductOrderCardAppData productCardData) {
        CardWidget<ProductOrderCardAppData> widget = productCardsScrollView.searchForWidgetByText(
                productCardData.getLmCode());
        anAssert.isNotNull(widget, String.format("Не найдена карточка товара с ЛМ %s", productCardData.getLmCode()),
                String.format("Карточка с ЛМ %s должна быть", productCardData.getLmCode()));
        ProductOrderCardAppData actualProductCardData = widget.collectDataFromPage();
        softAssert.isNull(actualProductCardData.getDiscountPercent(), "Обнаружена скидка % у товара с ЛМ" +
                actualProductCardData.getLmCode(), "Скидка должна отсутствовать");
        softAssert.isNull(actualProductCardData.getTotalPriceWithDiscount(),
                "Обнаружена сумма со скидкой у товара с ЛМ" +
                actualProductCardData.getLmCode(), "Сумма со скидкой должна отсутствовать");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что информация о заказе (единственном) в корзине должна быть ожидаемой (expectedOrderData)")
    public Cart35Page shouldOrderDataIs(OrderAppData expectedOrderData) {
        SalesDocumentData salesDocumentData = getSalesDocumentData();
        anAssert.isEquals(salesDocumentData.getOrderAppDataList().size(), 1,
                "Заказ должен быть только один");
        OrderAppData actualOrderData = salesDocumentData.getOrderAppDataList().get(0);
        actualOrderData.assertEqualsNotNullExpectedFields(expectedOrderData);
        return this;
    }

    @Step("Проверить, что данные корзины, как ожидались (expectedDocumentData)")
    public Cart35Page shouldSalesDocumentDataIs(SalesDocumentData expectedDocumentData) {
        SalesDocumentData salesDocumentData = getSalesDocumentData();
        salesDocumentData.assertEqualsNotNullExpectedFields(expectedDocumentData);
        return this;
    }

    @Step("Проверить, что в корзине нет товара с ЛМ кодом {expLmCode}")
    public Cart35Page shouldProductBeNotPresentInCart(String expLmCode) {
        List<ProductOrderCardAppData> productOrderCardAppDataList = productCardsScrollView.getFullDataList();
        for (ProductOrderCardAppData productCardData : productOrderCardAppDataList) {
            anAssert.isNotEquals(productCardData.getLmCode(), expLmCode,
                    "Продукта с таким ЛМ код быть не должно");
        }
        return this;
    }

}
