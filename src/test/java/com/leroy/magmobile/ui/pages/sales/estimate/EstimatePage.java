package com.leroy.magmobile.ui.pages.sales.estimate;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.elements.MagMobWhiteSubmitButton;
import com.leroy.magmobile.ui.models.CustomerData;
import com.leroy.magmobile.ui.models.sales.SalesOrderCardData;
import com.leroy.magmobile.ui.models.sales.SalesOrderData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.sales.basket.OrderRowProductWidget;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.search.widgets.SearchCustomerWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EstimatePage extends CommonMagMobilePage {

    public EstimatePage(Context context) {
        super(context);
    }

    @Builder
    @AllArgsConstructor
    public static class PageState {
        private boolean customerIsSelected;
        private boolean productIsAdded;
        private boolean estimateIsConfirmed;
    }

    public static boolean isThisPage(TestContext context) {
        return new Element(context.getDriver(),
                By.xpath("//*[@content-desc='EstimateDocumentScreenId']")).isVisible();
    }

    @Override
    public void waitForPageIsLoaded() {
        E("$EstimateDocumentScreenId", "EstimateDocumentScreen").waitForVisibility();
    }

    @AppFindBy(accessibilityId = "BackCloseMaster", metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(text = "Смета", metaName = "Заголовок экрана")
    Element headerLbl;

    @AppFindBy(xpath = "//*[@text='Смета']/following::android.view.ViewGroup[@content-desc='Button']",
            metaName = "Кнопка редактирования/удаления сметы")
    Element editTrashBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='EstimateDocumentScreenId']//android.widget.TextView[contains(@text, '№')]",
            metaName = "Номер документа")
    Element documentNumber;

    @AppFindBy(text = "Клиент", metaName = "Поле 'Клиент' (добавить)")
    Element selectCustomerBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='СМЕТА ДЛЯ КЛИЕНТА']]/following::android.widget.TextView",
            metaName = "Имя выбранного клиента")
    Element selectedCustomerName;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='СМЕТА ДЛЯ КЛИЕНТА']]/following::android.widget.TextView[contains(@text, '+7')]",
            metaName = "Номер телефона выбранного клиента")
    Element selectedCustomerPhoneNumber;

    @AppFindBy(xpath = "//android.widget.ScrollView//android.view.ViewGroup[android.widget.TextView[@index='2']]",
            metaName = "Поле 'Клиент' (клиент выбран)")
    SearchCustomerWidget customerWidget;

    @AppFindBy(xpath = "(//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]])[1]",
            metaName = "Карточка товара")
    OrderRowProductWidget productCardWidget;

    AndroidScrollView<SalesOrderCardData> orderCardDataScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]",
            OrderRowProductWidget.class);

    @AppFindBy(text = "ТОВАРЫ И УСЛУГИ", metaName = "Кнопка 'Товары и Услуги'")
    MagMobWhiteSubmitButton productAndServiceBtn;

    // Кнопка видна, когда Смета уже создана
    @AppFindBy(text = "ДЕЙСТВИЯ СО СМЕТОЙ")
    private MagMobGreenSubmitButton actionsWithEstimateBtn;

    // Bottom Area (It is visible when document is created)
    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/preceding-sibling::android.widget.TextView[2]",
            metaName = "Текст с кол-вом товара")
    Element countProductLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/preceding-sibling::android.widget.TextView[1]",
            metaName = "Текст с весом товара")
    Element weightProductLbl;

    @AppFindBy(text = "Итого: ")
    Element totalPriceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/following-sibling::android.widget.TextView")
    Element totalPriceVal;

    @AppFindBy(text = "ТОВАР")
    private MagMobWhiteSubmitButton addProductBtn;

    @AppFindBy(text = "СОЗДАТЬ")
    private MagMobGreenSubmitButton createBtn;

    @AppFindBy(text = "СОХРАНИТЬ")
    private MagMobGreenSubmitButton saveBtn;

    // ------ Grab info from Page methods -----------//

    /**
     * Получить Итоговую стоимость
     */
    public String getTotalPrice() {
        String _priceValue = totalPriceVal.getText().replaceAll("₽", "").trim();
        try {
            Double.parseDouble(_priceValue);
            return _priceValue;
        } catch (NumberFormatException err) {
            anAssert.isTrue(false, "Итого цена имеет не правильный формат: " + _priceValue);
            throw err;
        }
    }

    /**
     * Получить номер документа
     *
     * @param onlyDigits true - получить только номер из цифр. false - получить всю строку как есть
     * @return document number
     */
    public String getDocumentNumber(boolean onlyDigits) {
        if (onlyDigits)
            return documentNumber.getText().replaceAll("\\D+", "");
        else
            return documentNumber.getText();
    }

    @Step("Забираем со страницы имя выбранного клиента")
    public String getCustomerName() {
        return selectedCustomerName.getText();
    }

    @Step("Забираем со страницы номер телефона выбранного клиента")
    public String getCustomerPhoneNumber() {
        return selectedCustomerPhoneNumber.getText();
    }

    @Step("Забираем со страницы информацию о смете")
    public SalesOrderData getEstimateDataFromPage() {
        List<SalesOrderCardData> cardDataList = orderCardDataScrollView.getFullDataList();
        SalesOrderData orderData = new SalesOrderData();
        orderData.setOrderCardDataList(cardDataList);
        String ps = getPageSource();
        orderData.setTotalPrice(ParserUtil.strToDouble(totalPriceVal.getText(ps)));
        orderData.setProductCount(ParserUtil.strToInt(countProductLbl.getText(ps)));
        orderData.setTotalWeight(ParserUtil.strToDouble(weightProductLbl.getText(ps)));
        return orderData;
    }

    @Step("Получить список добавленных в смету карточек товаров/услуг с информацией о них")
    public List<SalesOrderCardData> getCardDataListFromPage() {
        return orderCardDataScrollView.getFullDataList();
    }

    // ACTIONS

    @Step("Нажать кнопку для редактирования сметы (перейти в режим редактирования)")
    public EstimatePage clickEditEstimateButton() {
        editTrashBtn.click();
        addProductBtn.waitForVisibility();
        return this;
    }

    @Step("Нажмите на {index}-ую карточку товара/услуги")
    public ActionWithProductCardModalPage clickCardByIndex(int index) throws Exception {
        index--;
        orderCardDataScrollView.clickElemByIndex(index);
        return new ActionWithProductCardModalPage(context);
    }

    @Step("Нажмите на поле 'Клиенты' (клиент не был выбран)")
    public SearchCustomerPage clickCustomerField() {
        selectCustomerBtn.click();
        return new SearchCustomerPage(context);
    }

    @Step("Нажмите на поле с Клиентом (клиент был выбран)")
    public EditCustomerModalPage clickEditCustomerField() {
        selectedCustomerName.click();
        return new EditCustomerModalPage(context);
    }

    @Step("Нажмите кнопку 'Товары и Услуги'")
    public SearchProductPage clickProductAndServiceButton() {
        productAndServiceBtn.click();
        return new SearchProductPage(context);
    }

    @Step("Нажмите кнопку '+Товар'")
    public SearchProductPage clickAddProductButton() {
        addProductBtn.click();
        return new SearchProductPage(context);
    }

    @Step("Нажмите кнопку 'Создать'")
    public EstimateSubmittedPage clickCreateButton() {
        createBtn.click();
        return new EstimateSubmittedPage(context);
    }

    @Step("Нажмите кнопку 'Действия со сметой'")
    public ActionsWithEstimateModalPage clickActionsWithEstimateButton() {
        actionsWithEstimateBtn.click();
        return new ActionsWithEstimateModalPage(context);
    }

    // VERIFICATIONS

    @Step("Проверить, что страница 'Смета' отображается корректно")
    public EstimatePage verifyRequiredElements(PageState state) {
        List<Element> expectedElements = new ArrayList<>(Arrays.asList(
                backBtn, headerLbl));
        if (!state.customerIsSelected)
            expectedElements.add(selectCustomerBtn);
        if (state.productIsAdded) {
            expectedElements.add(countProductLbl);
            expectedElements.add(weightProductLbl);
            expectedElements.add(totalPriceLbl);
            expectedElements.add(totalPriceVal);
            expectedElements.add(addProductBtn);
            if (state.estimateIsConfirmed)
                expectedElements.add(saveBtn);
            else
                expectedElements.add(createBtn);
            expectedElements.add(productCardWidget);
        } else {
            expectedElements.add(productAndServiceBtn);
        }
        softAssert.areElementsVisible(expectedElements.toArray(new Element[0]));
        if (!state.productIsAdded) {
            if (!state.customerIsSelected)
                softAssert.isFalse(productAndServiceBtn.isEnabled(),
                        "Кнопка '+ Товары и Услуги' активна");
            else
                softAssert.isTrue(productAndServiceBtn.isEnabled(),
                        "Кнопка '+ Товары и Услуги' неактивна");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент содержит следующие данные: {expectedCustomerData}")
    public EstimatePage shouldSelectedCustomerIs(CustomerData expectedCustomerData) {
        String ps = getPageSource();
        CustomerData actualCustomerData = customerWidget.collectDataFromPage(ps);
        softAssert.isEquals(actualCustomerData.getName(), expectedCustomerData.getName(),
                "Имя выбранного клиента неверно");
        if (expectedCustomerData.getCardNumber() != null)
            softAssert.isEquals(actualCustomerData.getCardNumber(), expectedCustomerData.getCardNumber(),
                    "Номер карты выбранного клиента неверен");
        if (expectedCustomerData.getCardType() != null)
            softAssert.isEquals(actualCustomerData.getCardType(), expectedCustomerData.getCardType(),
                    "Тип карты выбранного клиента неверен");
        if (expectedCustomerData.getPhone() != null) {
            String actualPhone = ParserUtil.standardPhoneFmt(actualCustomerData.getPhone());
            String expectedPhone = expectedCustomerData.getPhone();
            softAssert.isEquals(actualPhone, ParserUtil.standardPhoneFmt(expectedPhone),
                    "Телефон выбранного клиента неверен");
        }
        if (expectedCustomerData.getEmail() != null)
            softAssert.isEquals(actualCustomerData.getEmail(), expectedCustomerData.getEmail(),
                    "Email выбранного клиента неверен");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что смета находится в режиме редактирования")
    public EstimatePage shouldEditModeOn() {
        softAssert.areElementsVisible(addProductBtn, saveBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что смета содержит ожидаемые данные (expectedData)")
    public EstimatePage shouldEstimateDataIs(SalesOrderData expectedData) {
        SalesOrderData actualData = getEstimateDataFromPage();
        // Лучше расписать отдельно по полям потом:
        softAssert.isEquals(actualData, expectedData, "Ожидались другие данные в смете");
        softAssert.verifyAll();
        return this;
    }

}
