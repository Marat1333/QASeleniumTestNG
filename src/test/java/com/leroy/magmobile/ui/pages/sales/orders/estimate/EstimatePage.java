package com.leroy.magmobile.ui.pages.sales.orders.estimate;

import com.leroy.core.ContextProvider;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.elements.MagMobWhiteSubmitButton;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.models.sales.OrderAppData;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.sales.orders.CartOrderEstimatePage;
import com.leroy.magmobile.ui.pages.sales.widget.ProductOrderCardAppWidget;
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

public class EstimatePage extends CartOrderEstimatePage {

    @Builder
    @AllArgsConstructor
    public static class PageState {
        private boolean customerIsSelected;
        private boolean productIsAdded;
        private boolean confirmed;
        private boolean editModeOn;
        private boolean transformed;
    }

    public static boolean isThisPage() {
        return new Element(ContextProvider.getDriver(),
                By.xpath("//*[@content-desc='EstimateDocumentScreenId']")).isVisible();
    }

    @Override
    public void waitForPageIsLoaded() {
        anAssert.isTrue(E("$EstimateDocumentScreenId", "EstimateDocumentScreen").waitForVisibility(),
                "Страница Сметы не загрузилась");
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
    ProductOrderCardAppWidget productCardWidget;

    AndroidScrollView<ProductOrderCardAppData> productCardDataScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]",
            ProductOrderCardAppWidget.class);

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
    public Double getTotalPrice() {
        return ParserUtil.strToDouble(totalPriceVal.getText());
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
    public OrderAppData getOrderDataFromPage() throws Exception {
        List<ProductOrderCardAppData> cardDataList = productCardDataScrollView.getFullDataList();
        OrderAppData orderData = new OrderAppData();
        orderData.setProductCardDataList(cardDataList);
        String ps = getPageSource();
        orderData.setTotalPrice(ParserUtil.strToDouble(totalPriceVal.getText(ps)));
        orderData.setProductCount(ParserUtil.strToInt(countProductLbl.getText(ps)));
        orderData.setTotalWeight(ParserUtil.strToDouble(weightProductLbl.getText(ps)));
        return orderData;
    }

    @Step("Получить список добавленных в смету карточек товаров/услуг с информацией о них")
    public List<ProductOrderCardAppData> getCardDataListFromPage() throws Exception {
        return productCardDataScrollView.getFullDataList();
    }

    // ACTIONS

    @Step("Нажать кнопку для редактирования сметы (перейти в режим редактирования)")
    public EstimatePage clickEditEstimateButton() {
        editTrashBtn.click();
        addProductBtn.waitForVisibility();
        return this;
    }

    @Step("Нажмите на {index}-ую карточку товара/услуги")
    public EstimateActionWithProductCardModal clickCardByIndex(int index) throws Exception {
        index--;
        productCardDataScrollView.clickElemByIndex(index);
        return new EstimateActionWithProductCardModal();
    }

    @Step("Нажмите на поле 'Клиенты' (клиент не был выбран)")
    public SearchCustomerPage clickCustomerField() {
        selectCustomerBtn.click();
        return new SearchCustomerPage();
    }

    @Step("Нажмите на поле с Клиентом (клиент был выбран)")
    public EditCustomerModalPage clickEditCustomerField() {
        selectedCustomerName.click();
        return new EditCustomerModalPage();
    }

    @Step("Нажмите кнопку 'Товары и Услуги'")
    public SearchProductPage clickProductAndServiceButton() {
        productAndServiceBtn.click();
        return new SearchProductPage();
    }

    @Step("Нажмите кнопку '+Товар'")
    public SearchProductPage clickAddProductButton() {
        addProductBtn.click();
        return new SearchProductPage();
    }

    @Step("Нажмите кнопку 'Создать'")
    public EstimateSubmittedPage clickCreateButton() {
        createBtn.click();
        return new EstimateSubmittedPage();
    }

    @Step("Нажмите кнопку 'Сохранить'")
    public EstimatePage clickSaveButton() {
        saveBtn.click();
        saveBtn.waitForInvisibility();
        return this;
    }

    @Step("Нажмите кнопку 'Действия со сметой'")
    public ActionsWithEstimateModalPage clickActionsWithEstimateButton() {
        actionsWithEstimateBtn.click();
        return new ActionsWithEstimateModalPage();
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
            if (!state.transformed && (state.editModeOn || !state.confirmed))
                expectedElements.add(addProductBtn);
            if (state.editModeOn)
                expectedElements.add(saveBtn);
            else if (state.confirmed)
                expectedElements.add(actionsWithEstimateBtn);
            else if (!state.transformed)
                expectedElements.add(createBtn);
            expectedElements.add(productCardWidget);
        } else {
            expectedElements.add(productAndServiceBtn);
        }
        String ps = getPageSource();
        softAssert.areElementsVisible(ps, expectedElements.toArray(new Element[0]));
        if (!state.productIsAdded) {
            if (!state.customerIsSelected)
                softAssert.isFalse(productAndServiceBtn.isEnabled(),
                        "Кнопка '+ Товары и Услуги' активна");
            else
                softAssert.isTrue(productAndServiceBtn.isEnabled(),
                        "Кнопка '+ Товары и Услуги' неактивна");
        }
        if (state.transformed) {
            softAssert.isElementNotVisible(productAndServiceBtn, ps);
            softAssert.isElementNotVisible(actionsWithEstimateBtn, ps);
            softAssert.isElementNotVisible(addProductBtn, ps);
            softAssert.isElementNotVisible(editTrashBtn, ps);
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент содержит следующие данные: {expectedCustomerData}")
    public EstimatePage shouldSelectedCustomerIs(MagCustomerData expectedCustomerData) {
        String ps = getPageSource();
        MagCustomerData actualCustomerData = customerWidget.collectDataFromPage(ps);
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
    public EstimatePage shouldOrderDataIs(OrderAppData expectedData) throws Exception {
        OrderAppData actualData = getOrderDataFromPage();
        expectedData.getProductCardDataList().forEach(p -> p.setTotalStock(null));
        actualData.assertEqualsNotNullExpectedFields(expectedData);
        return this;
    }

}
