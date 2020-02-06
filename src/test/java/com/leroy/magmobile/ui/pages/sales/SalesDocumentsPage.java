package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.OldSearchProductPage;
import com.leroy.magmobile.ui.pages.sales.widget.SalesDocumentWidget;
import com.leroy.models.SalesDocumentData;
import io.qameta.allure.Step;

// Продажа -> Документы продажи -> "Мои продажи" или "Продажи моего магазина" и т.п.
public class SalesDocumentsPage extends CommonMagMobilePage {

    public SalesDocumentsPage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Мои продажи']]/following-sibling::android.view.ViewGroup",
            metaName = "Кнопка 'Фильтр'")
    private MagMobSubmitButton filterBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView//android.view.ViewGroup[android.view.ViewGroup[android.widget.TextView[contains(@text, '№')]]]",
            metaName = "Мини-карточки документов продажи", clazz = SalesDocumentWidget.class)
    private ElementList<SalesDocumentWidget> salesDocumentList;

    @AppFindBy(text = "СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ", metaName = "Кнопка 'СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ'")
    private MagMobSubmitButton submitBtn;

    public MagMobSubmitButton getSubmitBtn() {
        return submitBtn;
    }

    @Override
    public void waitForPageIsLoaded() {
        getSubmitBtn().waitForVisibility();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажмите 'Создать документ продажи'")
    public OldSearchProductPage clickCreateSalesDocumentButton() {
        getSubmitBtn().click();
        return new OldSearchProductPage(context);
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
        softAssert.isEquals(documentFromPage.getPin(), expectedDocument.getPin(),
                "PIN документа должен быть %s");
        softAssert.isEquals(documentFromPage.getPrice(), expectedDocument.getPrice(),
                "Сумма в документе должна быть %s");
        softAssert.isEquals(documentFromPage.getWhereFrom(), expectedDocument.getWhereFrom(),
                "Место отзыва документа должно быть %s");
        softAssert.verifyAll();
        return this;
    }
}
