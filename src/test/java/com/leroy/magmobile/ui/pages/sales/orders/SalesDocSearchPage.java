package com.leroy.magmobile.ui.pages.sales.orders;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.sales.ShortSalesDocumentData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.customers.ViewCustomerPage;
import com.leroy.magmobile.ui.pages.sales.widget.SalesDocumentWidget;
import io.qameta.allure.Step;

import java.util.List;

public abstract class SalesDocSearchPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "BackButton", metaName = "Кнопка назад")
    protected Element backBtn;

    @AppFindBy(accessibilityId = "ScreenTitle", metaName = "Загаловок экрана")
    protected Element title;

    AndroidScrollView<ShortSalesDocumentData> documentScrollList = new AndroidScrollView<>(
            driver, AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.view.ViewGroup[android.widget.TextView[contains(@text, '№')]]]",
            SalesDocumentWidget.class);

    @Override
    protected void waitForPageIsLoaded() {
        anAssert.isElementVisible(title, timeout);
    }

    // Actions

    @Step("Нажать кнопку назад")
    public ViewCustomerPage clickBackButton() {
        backBtn.click();
        return new ViewCustomerPage();
    }

    // Verifications

    @Step("Проверить, что в списке отображаются документы, которые ожидались")
    public SalesDocSearchPage shouldFirstDocumentsAre(List<ShortSalesDocumentData> expectedCarts) throws Exception {
        List<ShortSalesDocumentData> actualData = documentScrollList.getFullDataList(expectedCarts.size());
        anAssert.isEquals(actualData.size(), expectedCarts.size(), "Должно отображаться больше документов на экране");
        for (int i = 0; i < actualData.size(); i++) {
            actualData.get(i).assertEqualsNotNullExpectedFields(expectedCarts.get(i));
        }
        return this;
    }

}
