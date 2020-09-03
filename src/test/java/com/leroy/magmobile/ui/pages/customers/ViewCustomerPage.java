package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartSearchPage;
import com.leroy.magmobile.ui.pages.sales.orders.estimate.EstimateSearchPage;
import com.leroy.magmobile.ui.pages.sales.orders.order.OrderSearchPage;
import io.qameta.allure.Step;

public class ViewCustomerPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "BackButton", metaName = "Кнопка назад")
    Element backBtn;


    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Личные данные']]",
            metaName = "Личные данные")
    Element personalDataArea;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Корзины']]",
            metaName = "Корзины")
    Element cartsArea;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Корзины'] and android.view.ViewGroup[android.widget.TextView]]/android.view.ViewGroup/android.widget.TextView",
            metaName = "Счетчик 'Корзины'")
    Element cartsCount;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Документы продажи']]",
            metaName = "Документы продажи")
    Element salesDocArea;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Документы продажи'] and android.view.ViewGroup[android.widget.TextView]]/android.view.ViewGroup/android.widget.TextView",
            metaName = "Счетчик 'Документы продажи'")
    Element salesDocCount;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Сметы']]",
            metaName = "Сметы")
    Element estimatesArea;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Сметы'] and android.view.ViewGroup[android.widget.TextView]]/android.view.ViewGroup/android.widget.TextView",
            metaName = "Счетчик 'Сметы'")
    Element estimatesCount;

    @Override
    protected void waitForPageIsLoaded() {
        anAssert.isElementVisible(personalDataArea, timeout);
        anAssert.isElementVisible(salesDocArea, timeout);
    }

    // Actions

    @Step("Перейти в 'Личные данные'")
    public void goToPersonalData() {
        personalDataArea.click();
    }

    @Step("Перейти в 'Корзины'")
    public CartSearchPage goToCarts() {
        cartsArea.click();
        return new CartSearchPage();
    }

    @Step("Перейти в 'Документы продажи'")
    public OrderSearchPage goToSalesDocuments() {
        salesDocArea.click();
        return new OrderSearchPage();
    }

    @Step("Перейти в 'Сметы'")
    public EstimateSearchPage goToEstimates() {
        estimatesArea.click();
        return new EstimateSearchPage();
    }

    // Verifications

    @Step("Проверить, что страница 'Просмотр документа клиента' отображается корректно")
    public ViewCustomerPage verifyRequiredElements() {
        softAssert.areElementsVisible(personalDataArea, cartsArea, salesDocArea, estimatesArea);
        softAssert.verifyAll();
        return this;
    }

    private void shouldCountIs(int expectedCount, String document, Element elemCount) {
        String value = elemCount.getTextIfPresent();
        if (expectedCount == 0)
            anAssert.isTrue(value == null, "Счетчик у '" + document + "' должен отсутствовать");
        else if (expectedCount > 99)
            anAssert.isEquals(value, "99+", "Неверный счетчик кол-ва '" + document + "'");
        else
            anAssert.isEquals(value, String.valueOf(expectedCount), "Неверный счетчик кол-ва '" + document + "'");
    }

    @Step("Проверить, что количество корзин = {expectedCount}")
    public ViewCustomerPage shouldCartsCountIs(int expectedCount) {
        shouldCountIs(expectedCount, "Корзины", cartsCount);
        return this;
    }

    @Step("Проверить, что количество Документы продаж = {expectedCount}")
    public ViewCustomerPage shouldSalesDocCountIs(int expectedCount) {
        shouldCountIs(expectedCount, "Документы продаж", salesDocCount);
        return this;
    }

    @Step("Проверить, что количество Смет = {expectedCount}")
    public ViewCustomerPage shouldEstimatesCountIs(int expectedCount) {
        shouldCountIs(expectedCount, "Сметы", estimatesCount);
        return this;
    }

}
