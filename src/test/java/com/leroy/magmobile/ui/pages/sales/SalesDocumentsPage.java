package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.SearchProductPage;
import com.leroy.magmobile.ui.pages.sales.widget.SalesDocumentWidget;
import com.leroy.models.SalesDocumentData;
import io.qameta.allure.Step;

// Продажа -> Документы продажи -> "Мои продажи" или "Продажи моего магазина" и т.п.
// Или после того, как создали смету, то нажимаем "ПЕРЕЙТИ В СПИСОК ДОКУМЕНТОВ"
public class SalesDocumentsPage extends CommonMagMobilePage {

    public SalesDocumentsPage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Мои продажи']]/following-sibling::android.view.ViewGroup",
            metaName = "Кнопка 'Фильтр'")
    private MagMobGreenSubmitButton filterBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView//android.view.ViewGroup[android.view.ViewGroup[android.widget.TextView[contains(@text, '№')]]]",
            metaName = "Мини-карточки документов продажи", clazz = SalesDocumentWidget.class)
    private ElementList<SalesDocumentWidget> salesDocumentList;

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

    @Step("Проверить, что документ {index} содержит необходимую информацию: {expectedDocument}")
    public SalesDocumentsPage shouldSalesDocumentByIndexIs(int index, SalesDocumentData expectedDocument)
            throws Exception {
        SalesDocumentData documentFromPage = salesDocumentList.get(index).getSalesDocumentData();
        if (expectedDocument.getDate() != null) {
            softAssert.isEquals(documentFromPage.getDate(), expectedDocument.getDate(),
                    "Документ дата должна быть %s");
        }
        softAssert.isEquals(documentFromPage.getDocumentType(), expectedDocument.getDocumentType(),
                "Тип документа должен быть %s");
        // TODO можно будет подумать, чтоб не через contains, но чтоб C3201029 проходил:
        softAssert.isTrue(documentFromPage.getNumber().contains(expectedDocument.getNumber()),
                "Номер документа должен быть '" + expectedDocument.getNumber() + "'");
        if (expectedDocument.getPin() != null) {
            softAssert.isEquals(documentFromPage.getPin(), expectedDocument.getPin(),
                    "PIN документа должен быть %s");
        }
        softAssert.isEquals(documentFromPage.getPrice(), expectedDocument.getPrice(),
                "Сумма в документе должна быть %s");
        softAssert.isEquals(documentFromPage.getWhereFrom(), expectedDocument.getWhereFrom(),
                "Место отзыва документа должно быть %s");
        softAssert.verifyAll();
        return this;
    }
}
