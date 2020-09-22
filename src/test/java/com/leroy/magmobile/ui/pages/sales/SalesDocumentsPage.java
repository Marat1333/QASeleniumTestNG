package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.ContextProvider;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.models.sales.ShortSalesDocumentData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.sales.widget.SalesDocumentWidget;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;

import java.util.List;

// Продажа -> Документы продажи -> "Мои продажи" или "Продажи моего магазина" и т.п.
// Или после того, как создали смету, то нажимаем "ПЕРЕЙТИ В СПИСОК ДОКУМЕНТОВ"
public class SalesDocumentsPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "BackCloseModal", metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Мои продажи']]/following-sibling::android.view.ViewGroup",
            metaName = "Кнопка 'Фильтр'")
    private MagMobGreenSubmitButton filterBtn;

    AndroidScrollView<ShortSalesDocumentData> salesDocumentScrollList = new AndroidScrollView<>(
            driver, AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.view.ViewGroup[android.widget.TextView[contains(@text, '№')]]]",
            SalesDocumentWidget.class);

    @AppFindBy(text = "СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ", metaName = "Кнопка 'СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ'")
    private MagMobGreenSubmitButton createSalesDocumentBtn;

    @AppFindBy(text = "ОФОРМИТЬ ПРОДАЖУ", metaName = "Кнопка 'ОФОРМИТЬ ПРОДАЖУ'")
    private MagMobGreenSubmitButton makeSaleBtn;

    private MagMobGreenSubmitButton getSubmitBtn() {
        if (ContextProvider.getContext().isNewShopFunctionality())
            return makeSaleBtn;
        else
            return createSalesDocumentBtn;
    }

    @Override
    public void waitForPageIsLoaded() {
        anAssert.isTrue(getSubmitBtn().waitForVisibility(), "Страница 'Документы продажи' не загрузилась");
        waitUntilProgressBarIsInvisible();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажать кнопку для возвращения назад")
    public void clickBackButton() {
        backBtn.click();
    }

    @Step("Найти и выбрать документ, содержащий текст: {containsText}")
    public void searchForDocumentByTextAndSelectIt(String containsText, boolean updateDocumentListBefore) {
        CardWidget<ShortSalesDocumentData> cardWidget =
                salesDocumentScrollList.searchForWidgetByText(updateDocumentListBefore, containsText);
        anAssert.isNotNull(cardWidget, "Не нашли нужный документ",
                String.format("Документ содержащий текст %s должен быть найден",
                        containsText));
        cardWidget.click();
    }

    public void searchForDocumentByTextAndSelectIt(String containsText) {
        searchForDocumentByTextAndSelectIt(containsText, false);
    }

    @Step("Ждем, пока документ №{docNumber} не будет в состоянии {expectedStatus}")
    public SalesDocumentsPage waitUntilDocumentIsInCorrectStatus(String docNumber, String expectedStatus) {
        String actualStatus;
        int iCount = 0;
        do {
            if (iCount > 0) {
                salesDocumentScrollList.scrollToBeginning();
                waitUntilProgressBarAppearsAndDisappear();
            }
            CardWidget<ShortSalesDocumentData> cardWidget =
                    salesDocumentScrollList.searchForWidgetByText(true, docNumber);
            anAssert.isNotNull(cardWidget, "Не нашли документ " + docNumber,
                    String.format("Документ №%s должен быть найден",
                            docNumber));
            actualStatus = cardWidget.collectDataFromPage().getDocumentState();
            iCount++;
        } while (!actualStatus.equals(expectedStatus) && iCount < 15);
        anAssert.isEquals(actualStatus, expectedStatus, "Не смогли дождаться");
        return this;
    }

    @Step("Нажмите кнопку 'Создать документ продажи'")
    public SearchProductPage clickCreateSalesDocumentButton() {
        createSalesDocumentBtn.click();
        return new SearchProductPage();
    }

    @Step("Нажмите кнопку 'Оформить продажу'")
    public SearchProductPage clickMakeSaleButton() {
        makeSaleBtn.click();
        return new SearchProductPage();
    }

    /* ---------------------- Verifications -------------------------- */

    @Step("Проверить, что страница Документы продажи отображается корректно")
    public SalesDocumentsPage verifyRequiredElements() {
        softAssert.areElementsVisible(filterBtn, getSubmitBtn());
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что документ на странице имеется документ с данными: {expectedDocument}")
    public SalesDocumentsPage shouldSalesDocumentIsPresentAndDataMatches(
            ShortSalesDocumentData expectedDocument, boolean updateScreen) {
        CardWidget<ShortSalesDocumentData> widget = salesDocumentScrollList.searchForWidgetByText(
                updateScreen, expectedDocument.getNumber());
        anAssert.isNotNull(widget,
                "Документ " + expectedDocument.getNumber() + " не найден",
                "Документ " + expectedDocument.getNumber() + " должен присутствовать на странице");
        ShortSalesDocumentData documentFromPage = widget.collectDataFromPage();
        if (expectedDocument.getDate() != null) {
            softAssert.isEquals(documentFromPage.getDate(), expectedDocument.getDate(),
                    "Документ дата - не верна");
        }
        if (expectedDocument.getDocumentState() != null)
            softAssert.isEquals(documentFromPage.getDocumentState(), expectedDocument.getDocumentState(),
                    "Неверный Тип документа");
        // TODO можно будет подумать, чтоб не через contains, но чтоб C3201029 проходил:
        softAssert.isTrue(documentFromPage.getNumber().contains(expectedDocument.getNumber()),
                "Номер документа должен быть '" + expectedDocument.getNumber() + "'");
        if (expectedDocument.getPin() != null) {
            if (expectedDocument.getPin().isEmpty())
                softAssert.isNull(documentFromPage.getPin(), "PIN код документа виден",
                        "PIN код документа не должно быть");
            else if (documentFromPage.getPin() == null)
                softAssert.isTrue(false, "PIN код документа отсутствует");
            else
                softAssert.isEquals(documentFromPage.getPin(), expectedDocument.getPin(),
                        "PIN документа должен быть %s");
        }
        if (expectedDocument.getCustomerName() != null) {
            softAssert.isEquals(documentFromPage.getCustomerName(), expectedDocument.getCustomerName(),
                    "Неверное имя клиента");
        }
        if (expectedDocument.getDocumentTotalPrice() != null) {
            softAssert.isEquals(documentFromPage.getDocumentTotalPrice(), expectedDocument.getDocumentTotalPrice(),
                    "Сумма в документе - не верна");
        }
        softAssert.isEquals(documentFromPage.getTitle(), expectedDocument.getTitle(),
                "Место отзыва документа - не верно");
        softAssert.verifyAll();
        return this;
    }

    public SalesDocumentsPage shouldSalesDocumentIsPresentAndDataMatches(ShortSalesDocumentData expectedDocument) {
        return shouldSalesDocumentIsPresentAndDataMatches(expectedDocument, false);
    }

    @Step("Проверить, что среди последних 5 документов, документа с номером {expDocNumber} на странице нет")
    public SalesDocumentsPage shouldSalesDocumentIsNotPresent(String expDocNumber) throws Exception {
        List<ShortSalesDocumentData> shortSalesDocumentDataList = salesDocumentScrollList
                .getFullDataList(5, true);
        anAssert.isTrue(shortSalesDocumentDataList.size() > 0,
                "На странице нет ни одного документа");
        for (ShortSalesDocumentData data : shortSalesDocumentDataList) {
            anAssert.isNotEquals(data.getNumber(), expDocNumber,
                    "Документ с соответсвующим номером найден");
        }
        return this;
    }
}
