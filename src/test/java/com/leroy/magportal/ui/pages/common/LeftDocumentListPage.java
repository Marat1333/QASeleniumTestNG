package com.leroy.magportal.ui.pages.common;

import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.models.salesdoc.IDataWithNumberAndStatus;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class LeftDocumentListPage<W extends CardWebWidget<D>, D extends IDataWithNumberAndStatus>
        extends MagPortalBasePage {

    @Override
    public void waitForPageIsLoaded() {
        refreshDocumentListBtn().waitForVisibility();
        waitForSpinnerDisappear();
    }

    protected abstract Button refreshDocumentListBtn();

    protected abstract CardWebWidgetList<W, D> documentCardList();

    // Grab info

    @Step("Получить информацию о документах в списке слева")
    public List<D> getDocumentDataList() throws Exception {
        return documentCardList().getDataList();
    }

    @Step("Получить кол-во документов в списке слева")
    public int getDocumentCount() {
        return documentCardList().getCount();
    }

    // Actions

    @Step("Обновить список документов")
    public void refreshDocumentList() {
        refreshDocumentListBtn().click();
        waitForSpinnerAppearAndDisappear();
    }

    @Step("Выберите документ в списке слева")
    public void clickDocumentInLeftMenu(String number) throws Exception {
        for (W widget : documentCardList()) {
            D data = widget.collectDataFromPage();
            if (data.getNumber().equals(number)) {
                widget.click();
                break;
            }
        }
        waitForSpinnerDisappear();
    }

    // Verifications

    private boolean isDocumentPresentInList(String number) throws Exception {
        for (D docData : documentCardList().getDataList()) {
            if (ParserUtil.strWithOnlyDigits(docData.getNumber()).equals(number)) {
                return true;
            }
        }
        return false;
    }

    @Step("Проверить, что документ №{number} в списке слева отображается")
    public void shouldDocumentIsPresent(String number) throws Exception {
        anAssert.isTrue(isDocumentPresentInList(number),
                "Документ №" + number + " не найден в списке слева");
    }

    @Step("Проверить, что документ №{number} в списке слева не отображается")
    public void shouldDocumentIsNotPresent(String number) throws Exception {
        anAssert.isFalse(isDocumentPresentInList(number),
                "Документ №" + number + " найден в списке слева");
    }

    @Step("Проверить, что в списке документов слева присутствуют только имеющие статусы: {statuses}")
    public void shouldDocumentListContainsOnlyWithStatuses(String... statuses) throws Exception {
        Set<String> actualStatuses = new HashSet<>();
        for (D docData : documentCardList().getDataList()) {
            actualStatuses.add(docData.getStatus());
        }
        actualStatuses.removeAll(Arrays.asList(statuses));
        anAssert.isTrue(actualStatuses.isEmpty(),
                "В списке слева обнаружены документы, которых быть не должно, со статусами:" +
                        actualStatuses.toString());
    }

    @Step("Проверить, что в списке документов слева присутствуют только содержащие номер: {value}")
    public LeftDocumentListPage<W, D> shouldDocumentListHaveNumberContains(String value) throws Exception {
        for (D docData : documentCardList().getDataList()) {
            softAssert.isTrue(docData.getNumber().contains(value),
                    docData.getNumber() + " документ не содержит " + value);
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что в списке документов слева присутствуют нужные документы (expectedDocuments)")
    public void shouldDocumentListIs(List<D> expectedDocuments) throws Exception {
        anAssert.isEquals(documentCardList().getDataList(), expectedDocuments,
                "Ожидались другие документы");
    }

    @Step("Проверить, что в списке документов слева на текущей странице отображается {value} документов")
    public void shouldDocumentCountIs(int value) throws Exception {
        anAssert.isEquals(documentCardList().getCount(), value,
                "Ожидалось другое кол-во документов");
    }

    @Step("Проверить, что в списке документов слева присутствуют документы с номерами: {expectedNumbers}")
    public void shouldDocumentListNumbersEqual(List<String> expectedNumbers) throws Exception {
        anAssert.isEquals(documentCardList().getDataList().stream().map(
                d -> ParserUtil.strWithOnlyDigits(d.getNumber()))
                        .collect(Collectors.toList()), expectedNumbers,
                "Ожидались другие номера документов");
    }

    @Step("Проверить, что в списке документов слева другие документы, отличные от : {expectedNumbers}")
    public void shouldDocumentListNumbersNotEqual(List<String> expectedNumbers) throws Exception {
        List<String> actualNumbers = documentCardList().getDataList().stream().map(
                d -> ParserUtil.strWithOnlyDigits(d.getNumber()))
                .collect(Collectors.toList());
        anAssert.isTrue(actualNumbers.size() > 0, "Не найдено ни одного номера документа");
        anAssert.isNotEquals(actualNumbers, expectedNumbers,
                "Номера документов в списке остались прежними (не изменились)");
    }

    @Step("Проверить, что в списке документов слева присутствуют документы, содержащие номер: {expectedNumber}")
    public void shouldDocumentListFilteredByNumber(String expectedNumber) throws Exception {
        List<String> actualDocumentNumbers = documentCardList().getDataList().stream().map(D::getNumber)
                .collect(Collectors.toList());
        anAssert.isTrue(actualDocumentNumbers.size() > 0,
                "Не найден ни один документ");
        for (String docNumber : actualDocumentNumbers) {
            anAssert.isTrue(ParserUtil.strWithOnlyDigits(docNumber).contains(expectedNumber),
                    String.format("Номер документа %s не содержит %s", docNumber, expectedNumber));
        }
    }

    @Step("Проверить, что в список документов слева пуст")
    public void shouldDocumentListIsEmpty() throws Exception {
        anAssert.isTrue(documentCardList().getDataList().size() == 0,
                "Список документов не пустой (содержит документы)");
    }

    @Step("Проверить, что список документов слева содержит хотя бы один документ")
    public void shouldDocumentListIsNotEmpty() throws Exception {
        anAssert.isTrue(documentCardList().getDataList().size() > 0,
                "Список документов пустой");
    }
}
