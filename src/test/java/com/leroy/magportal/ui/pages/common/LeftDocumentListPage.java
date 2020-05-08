package com.leroy.magportal.ui.pages.common;

import com.leroy.core.web_elements.general.Button;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.models.salesdoc.IDataWithNumberAndStatus;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import io.qameta.allure.Step;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class LeftDocumentListPage<W extends CardWebWidget<D>, D extends IDataWithNumberAndStatus>
        extends MenuPage {

    public LeftDocumentListPage(Context context) {
        super(context);
    }

    @Override
    public void waitForPageIsLoaded() {
        refreshDocumentListBtn().waitForVisibility();
        waitForSpinnerDisappear();
    }

    protected abstract Button refreshDocumentListBtn();

    protected abstract CardWebWidgetList<W, D> documentCardList();

    // Grab info

    @Step("Получить информацию о документах в списке слева")
    public List<D> getDocumentDataList() {
        return documentCardList().getDataList();
    }

    // Actions

    @Step("Обновить список документов")
    public void refreshDocumentList() {
        refreshDocumentListBtn().click();
        waitForSpinnerAppearAndDisappear();
    }

    // Verifications

    private boolean isDocumentPresentInList(String number) {
        for (D docData : documentCardList().getDataList()) {
            if (docData.getNumber().equals(number)) {
                return true;
            }
        }
        return false;
    }

    @Step("Проверить, что документ №{number} в списке слева отображается")
    public void shouldDocumentIsPresent(String number) {
        anAssert.isTrue(isDocumentPresentInList(number),
                "Документ №" + number + " не найден в списке слева");
    }

    @Step("Проверить, что документ №{number} в списке слева не отображается")
    public void shouldDocumentIsNotPresent(String number) throws Exception {
        anAssert.isFalse(isDocumentPresentInList(number),
                "Документ №" + number + " найден в списке слева");
    }

    @Step("Проверить, что в списке документов слева присутствуют только имеющие статусы: {statuses}")
    public void shouldDocumentListContainsOnlyWithStatuses(String... statuses) {
        Set<String> actualStatuses = new HashSet<>();
        for (D docData : documentCardList().getDataList()) {
            actualStatuses.add(docData.getStatus());
        }
        actualStatuses.removeAll(Arrays.asList(statuses));
        anAssert.isTrue(actualStatuses.isEmpty(),
                "В списке слева обнаружены документы, которых быть не должно, со статусами:" +
                        actualStatuses.toString());
    }

    @Step("Проверить, что в списке документов слева присутствуют нужные документы (expectedDocuments)")
    public void shouldDocumentListIs(List<D> expectedDocuments) {
        anAssert.isEquals(documentCardList().getDataList(), expectedDocuments,
                "Ожидались другие документы");
    }
}
