package com.leroy.magportal.ui.pages.common;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.models.salesdoc.ShortSalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.widget.ShortDocumentCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import io.qameta.allure.Step;

public class LeftDocumentListPage extends MenuPage {

    public LeftDocumentListPage(Context context) {
        super(context);
    }

    @WebFindBy(xpath = "//div[contains(@class, 'Refresh-banner')]//button",
            metaName = "Кнопка Обновить список документов")
    Button refreshDocumentListBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Documents-ListItemCard__content')]",
            clazz = ShortDocumentCardWidget.class)
    CardWebWidgetList<ShortDocumentCardWidget, ShortSalesDocWebData> documentCardList;

    // Actions

    @Step("Обновить список документов")
    public void refreshDocumentList() {
        refreshDocumentListBtn.click();
        waitForSpinnerAppearAndDisappear();
    }

    // Verifications

    private boolean isDocumentPresentInList(String number) {
        for (ShortSalesDocWebData docData : documentCardList.getDataList()) {
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
}
