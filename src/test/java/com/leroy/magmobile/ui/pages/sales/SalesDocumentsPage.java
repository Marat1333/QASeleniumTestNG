package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import com.leroy.magmobile.ui.pages.common.OldSearchProductPage;
import com.leroy.magmobile.ui.pages.common.TopMenuPage;
import com.leroy.magmobile.ui.pages.sales.widget.SalesDocumentWidget;
import com.leroy.models.SalesDocumentData;
import io.qameta.allure.Step;

import java.util.List;

public class SalesDocumentsPage extends TopMenuPage {

    public SalesDocumentsPage(TestContext context) {
        super(context);
    }

    @AppFindBy(text = "Фильтр", metaName = "Кнопка 'Фильтр'")
    private MagMobSubmitButton filterBtn;

    @AppFindBy(text = "СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ", metaName = "Кнопка 'СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ'")
    private MagMobSubmitButton createSalesDocumentBtn;

    @AppFindBy(xpath = "//android.widget.HorizontalScrollView//android.widget.TextView")
    private ElementList<Element> currentFilters;

    @AppFindBy(xpath = "//android.view.ViewGroup[not(@index='0') and child::android.view.ViewGroup[@content-desc='lmui-Icon']]/../../android.view.ViewGroup[not(android.widget.TextView[@text='СЕГОДНЯ'])]",
            metaName = "Мини-карточки документов продажи", clazz = SalesDocumentWidget.class)
    private ElementList<SalesDocumentWidget> salesDocumentList;

    @Override
    public void waitForPageIsLoaded() {
        filterBtn.waitForVisibility();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажмите 'Создать документ продажи'")
    public OldSearchProductPage clickCreateSalesDocumentButton() {
        createSalesDocumentBtn.click();
        return new OldSearchProductPage(context);
    }


    /* ---------------------- Verifications -------------------------- */

    public SalesDocumentsPage verifyRequiredElements() {
        softAssert.isElementVisible(filterBtn);
        softAssert.isElementVisible(createSalesDocumentBtn);
        softAssert.verifyAll();
        return this;
    }

    public SalesDocumentsPage shouldFilterIs(String textFilter) throws Exception {
        anAssert.isTrue(currentFilters.getCount() > 0,
                "Должен быть виден хотя бы один фильтр");
        anAssert.isEquals(currentFilters.get(0).getText(), textFilter,
                "Текущий фильтр должен быть выставлен в %");
        return this;
    }

    public SalesDocumentsPage shouldFiltersAre(List<String> textFilters) throws Exception {
        anAssert.isEquals(currentFilters.getTextList(), textFilters,
                "Текущие фильтры должны быть такими: %");
        return this;
    }

    public SalesDocumentsPage shouldSalesDocumentByIndexIs(int index, SalesDocumentData expectedDocument)
            throws Exception {
        SalesDocumentData documentFromPage = salesDocumentList.get(index).getSalesDocumentData();
        if (expectedDocument.getDate() != null) {
            softAssert.isEquals(documentFromPage.getDate(), expectedDocument.getDate(),
                    "Документ дата должна быть %s");
        }
        softAssert.isEquals(documentFromPage.getDocumentType(), expectedDocument.getDocumentType(),
                "Тип документа должен быть %s");
        softAssert.isEquals(documentFromPage.getNumber(), expectedDocument.getNumber(),
                "Номер документа должен быть %s");
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
