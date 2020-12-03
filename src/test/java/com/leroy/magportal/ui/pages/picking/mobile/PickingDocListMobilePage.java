package com.leroy.magportal.ui.pages.picking.mobile;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.picking.ShortPickingTaskData;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.pages.picking.widget.ShortPickingTaskCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import io.qameta.allure.Step;

import java.util.List;
import java.util.stream.Collectors;

public class PickingDocListMobilePage extends MagPortalBasePage {

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - string-length('Picking-PickingListItem') +1) = 'Picking-PickingListItem' or contains(@class, 'Picking-PickingListItem__active')  or contains(@class, 'Picking-PickingListItem highPriority')]",
            clazz = ShortPickingTaskCardWidget.class)
    private CardWebWidgetList<ShortPickingTaskCardWidget, ShortPickingTaskData> documentCardList;

    @WebFindBy(xpath = "//button[contains(.,'Показать')]", metaName = "Кнопка 'Показать'")
    Element showBtn;

    @WebFindBy(xpath = "//button[contains(@class, 'Picking-PickingListItem__pickingCheckbox')]",
            metaName = "Чек-боксы 'Выбрать для сборки'")
    ElementList<Element> selectForPickingChkBoxes;

    @WebFindBy(xpath = "//div[contains(@class, 'PickingQuickFilterBlock__quickFilter')]/div[3]/div[1]",
            metaName = "Иконка волны сборок")
    Element pickingWaveBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'PickingQuickFilterBlock__quickFilter')]/div[3]/button",
            metaName = "Иконка поиска")
    Element magnifierBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'PickingQuickFilterBlock__quickFilter')]//input",
            metaName = "Поле поиска")
    EditBox searchFld;

    @WebFindBy(xpath = "//div[contains(@class, 'PickingHeader__filterButton')]//button", metaName = "Кнопка 'Фильтр'")
    Element filterBtn;

    @WebFindBy(xpath = "//button[contains(@class, 'PickingList__action-button') and contains(., 'Отмена')]",
            metaName = "Кнопка 'Отмена'")
    Element cancelBtn;

    @WebFindBy(xpath = "//button[contains(@class, 'PickingList__action-button') and contains(., 'Начать сборку')]",
            metaName = "Кнопка 'Начать сборку'")
    Element startPickingBtn;

    @Override
    protected void waitForPageIsLoaded() {
        filterBtn.waitForVisibility();
        waitForSpinnerDisappear();
    }

    // Grab data

    @Step("Получить информацию о документах в списке слева")
    public List<ShortPickingTaskData> getDocumentDataList() throws Exception {
        return documentCardList.getDataList();
    }

    // Actions

    @Step("Нажать кнопку отмена")
    public PickingDocListMobilePage clickCancelButton() {
        cancelBtn.click();
        return this;
    }

    @Step("Нажать кнопку 'Начать сборку'")
    public PickingDocListMobilePage clickStartPicking() {
        startPickingBtn.click();
        return new PickingDocListMobilePage();
    }

    @Step("Нажать кнопку 'Показать'")
    public PickingDocListMobilePage clickShowButton() {
        showBtn.clickJS();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Выберите документ в списке слева")
    public void clickDocumentInLeftMenu(String number) throws Exception {
        boolean result = false;
        for (ShortPickingTaskCardWidget widget : documentCardList) {
            ShortPickingTaskData data = widget.collectDataFromPage();
            if (data.getNumber().equals(number)) {
                widget.click();
                result = true;
                break;
            }
        }
        anAssert.isTrue(result, String.format("Документ с номером %s не найдено", number));
        waitForSpinnerDisappear();
    }

    @Step("Отметить все сборки 'Для сборки'")
    public PickingDocListMobilePage selectAllPickingChkBoxes() {
        selectForPickingChkBoxes.waitUntilAtLeastOneElementIsPresent();
        for (Element chkBox : selectForPickingChkBoxes) {
            chkBox.click();
        }
        return this;
    }

    @Step("Нажать кнопку Фильтры")
    public PickingFilterMobilePage clickFilterButton() {
        filterBtn.click();
        return new PickingFilterMobilePage();
    }

    @Step("Нажать иконку волны сборок")
    public PickingWaveMobilePage clickPickingWageButton() {
        pickingWaveBtn.click();
        return new PickingWaveMobilePage();
    }

    @Step("Поиск сборки по номеру заказа")
    public PickingDocListMobilePage searchForPickingByOrderNumber(String orderNumber) {
        magnifierBtn.click();
        searchFld.clearFillAndSubmit(orderNumber);
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    // Verifications

    @Step("Проверить, что все документы отфильтрованы по определенному типу сборки")
    public PickingDocListMobilePage shouldDocumentsFilteredByAssemblyType(PickingConst.AssemblyType assemblyType)
            throws Exception {
        List<ShortPickingTaskData> actualData = getDocumentDataList();
        anAssert.isTrue(actualData.size() > 0, "Не найден ни один документ");
        for (ShortPickingTaskData shortPickingTaskData : actualData) {
            softAssert.isEquals(shortPickingTaskData.getAssemblyType(), assemblyType,
                    "Документ " + shortPickingTaskData.getNumber() + " имеет другой тип сборки");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что все документы отфильтрованы по определенному статусу сборки")
    public PickingDocListMobilePage shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus... statuses)
            throws Exception {
        List<ShortPickingTaskData> actualData = getDocumentDataList();
        anAssert.isTrue(actualData.size() > 0, "Не найден ни один документ");
        for (ShortPickingTaskData shortPickingTaskData : actualData) {
            boolean result = false;
            for (SalesDocumentsConst.PickingStatus status : statuses) {
                if (shortPickingTaskData.getStatus().contains(status.getUiVal()))
                    result = true;
            }
            softAssert.isTrue(result,
                    "Документ " + shortPickingTaskData.getNumber() + " имеет другой статус сборки: " +
                            shortPickingTaskData.getStatus());
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что все документы отфильтрованы, где сборщик Я")
    public PickingDocListMobilePage shouldDocumentsFilteredByMy()
            throws Exception {
        List<ShortPickingTaskData> actualData = getDocumentDataList();
        anAssert.isTrue(actualData.size() > 0, "Не найден ни один документ");
        for (ShortPickingTaskData shortPickingTaskData : actualData) {
            if (shortPickingTaskData.getCollector() == null) {
                softAssert.isTrue(false,
                        "У документа " + shortPickingTaskData.getNumber() + " отсутствует сборщик");
            } else {
                softAssert.isEquals(shortPickingTaskData.getCollector(), "Я",
                        "Документ " + shortPickingTaskData.getNumber() + " имеет другого сборщика");
            }
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что все документы отфильтрованы по определенному отделу")
    public PickingDocListMobilePage shouldDocumentsFilteredByDepartments(String... departments)
            throws Exception {
        List<ShortPickingTaskData> actualData = getDocumentDataList();
        anAssert.isTrue(actualData.size() > 0, "Не найден ни один документ");
        for (ShortPickingTaskData shortPickingTaskData : actualData) {
            boolean result = false;
            for (String dep : departments) {
                if (shortPickingTaskData.getDepartments().contains(Integer.parseInt(dep))) {
                    result = true;
                    break;
                }
            }
            softAssert.isTrue(result,
                    "В документе " + shortPickingTaskData.getNumber() + " ожидался другой отдел");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что в списке документов присутствуют нужные номера документов")
    public PickingDocListMobilePage shouldDocumentListIs(List<String> expectedDocNumbers) throws Exception {
        anAssert.isEquals(getDocumentDataList().stream().map(ShortPickingTaskData::getNumber)
                        .collect(Collectors.toList()), expectedDocNumbers,
                "Ожидались другие документы");
        return this;
    }

    @Step("Проверить видимость активных кнопок (начать сборку и отмена)")
    public PickingDocListMobilePage checkActiveButtonsVisibility(boolean shouldBeVisible) {
        if (shouldBeVisible)
            anAssert.isTrue(cancelBtn.isVisible() && startPickingBtn.isVisible(),
                    "Кнопки 'Начать сборку' и 'Отмена' не отображаются");
        else
            anAssert.isTrue(!cancelBtn.isVisible() && !startPickingBtn.isVisible(),
                    "Кнопки 'Начать сборку' и 'Отмена' отображаются");
        return this;
    }

}
