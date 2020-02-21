package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.SearchProductPage;
import com.leroy.magmobile.ui.pages.sales.widget.SalesDocumentWidget;
import com.leroy.magmobile.ui.pages.widgets.CardWidget;
import com.leroy.models.SalesDocumentData;
import io.qameta.allure.Step;

// Продажа -> Документы продажи -> "Мои продажи" или "Продажи моего магазина" и т.п.
// Или после того, как создали смету, то нажимаем "ПЕРЕЙТИ В СПИСОК ДОКУМЕНТОВ"
public class SalesDocumentsPage extends CommonMagMobilePage {

    public SalesDocumentsPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "BackCloseModal", metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Мои продажи']]/following-sibling::android.view.ViewGroup",
            metaName = "Кнопка 'Фильтр'")
    private MagMobGreenSubmitButton filterBtn;

    AndroidScrollView<SalesDocumentData> salesDocumentScrollList = new AndroidScrollView<>(
            driver, AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.view.ViewGroup[android.widget.TextView[contains(@text, '№')]]]",
            SalesDocumentWidget.class);

    @AppFindBy(text = "СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ", metaName = "Кнопка 'СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ'")
    private MagMobGreenSubmitButton createSalesDocumentBtn;

    @AppFindBy(text = "ОФОРМИТЬ ПРОДАЖУ", metaName = "Кнопка 'ОФОРМИТЬ ПРОДАЖУ'")
    private MagMobGreenSubmitButton makeSaleBtn;

    private MagMobGreenSubmitButton getSubmitBtn() {
        if (context.isIs35Shop())
            return makeSaleBtn;
        else
            return createSalesDocumentBtn;
    }

    @Override
    public void waitForPageIsLoaded() {
        getSubmitBtn().waitForVisibility();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажать кнопку для возвращения назад")
    public void clickBackButton() {
        backBtn.click();
    }

    @Step("Найти и выбрать документ, содержащий текст: {containsText}")
    public void searchForDocumentByTextAndSelectIt(String containsText) {
        CardWidget<SalesDocumentData> cardWidget =
                salesDocumentScrollList.searchForWidgetByText(containsText);
        anAssert.isNotNull(cardWidget, "Не нашли нужный документ",
                String.format("Документ содержащий текст %s должен быть найден",
                        containsText));
        cardWidget.click();
    }

    @Step("Нажмите кнопку 'Создать документ продажи'")
    public SearchProductPage clickCreateSalesDocumentButton() {
        createSalesDocumentBtn.click();
        return new SearchProductPage(context);
    }

    @Step("Нажмите кнопку 'Оформить продажу'")
    public SearchProductPage clickMakeSaleButton() {
        makeSaleBtn.click();
        return new SearchProductPage(context);
    }

    /* ---------------------- Verifications -------------------------- */

    @Step("Проверить, что страница Документы продажи отображается корректно")
    public SalesDocumentsPage verifyRequiredElements() {
        softAssert.areElementsVisible(filterBtn, getSubmitBtn());
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что документ на странице имеется документ с данными: {expectedDocument}")
    public SalesDocumentsPage shouldSalesDocumentIsPresentAndDataMatches(SalesDocumentData expectedDocument) {
        CardWidget<SalesDocumentData> widget = salesDocumentScrollList.searchForWidgetByText(
                expectedDocument.getNumber());
        anAssert.isNotNull(widget,
                "Документ " + expectedDocument.getNumber() + " не найден",
                "Документ " + expectedDocument.getNumber() + " должен присутствовать на странице");
        SalesDocumentData documentFromPage = widget.collectDataFromPage();
        if (expectedDocument.getDate() != null) {
            softAssert.isEquals(documentFromPage.getDate(), expectedDocument.getDate(),
                    "Документ дата должна быть %s");
        }
        if (expectedDocument.getDocumentState() != null)
            softAssert.isEquals(documentFromPage.getDocumentState(), expectedDocument.getDocumentState(),
                    "Неверный Тип документа");
        // TODO можно будет подумать, чтоб не через contains, но чтоб C3201029 проходил:
        softAssert.isTrue(documentFromPage.getNumber().contains(expectedDocument.getNumber()),
                "Номер документа должен быть '" + expectedDocument.getNumber() + "'");
        if (expectedDocument.getPin() != null) {
            if (documentFromPage.getPin() == null)
                softAssert.isTrue(false, "PIN код документа отсутствует");
            else
                softAssert.isEquals(documentFromPage.getPin(), expectedDocument.getPin(),
                        "PIN документа должен быть %s");
        }
        softAssert.isEquals(documentFromPage.getPrice(), expectedDocument.getPrice(),
                "Сумма в документе должна быть %s");
        softAssert.isEquals(documentFromPage.getTitle(), expectedDocument.getTitle(),
                "Место отзыва документа должно быть %s");
        softAssert.verifyAll();
        return this;
    }
}
