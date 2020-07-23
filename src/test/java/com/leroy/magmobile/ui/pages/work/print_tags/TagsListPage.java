package com.leroy.magmobile.ui.pages.work.print_tags;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.print_tags.data.ProductTagData;
import com.leroy.magmobile.ui.pages.work.print_tags.modal.ConfirmSessionExitModalPage;
import com.leroy.magmobile.ui.pages.work.print_tags.widgets.ProductWidget;
import io.qameta.allure.Step;

import java.util.List;

public class TagsListPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "Button")
    Button backBtn;

    @AppFindBy(accessibilityId = "ScreenTitle")
    Element createSessionTimeStamp;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"ScreenHeader\"]/android.view.ViewGroup[1]")
    Button deleteSessionBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"ScreenHeader\"]/android.view.ViewGroup[2]")
    Button switchToMassiveEditorModeBtn;

    AndroidScrollView<ProductTagData> productsScrollView = new AndroidScrollView<ProductTagData>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, ".//android.view.ViewGroup[@content-desc=\"lmCode\"]/..",
            ProductWidget.class);

    @Step("Перейти назад")
    public ConfirmSessionExitModalPage goBack() {
        backBtn.click();
        return new ConfirmSessionExitModalPage();
    }

    @Step("Проверить, что список содержит все переданные товары")
    public TagsListPage shouldProductsAreCorrect(String...lmCodes) {
        List<ProductTagData> productTagsList = productsScrollView.getFullDataList(lmCodes.length);
        for (int i = 0; i < productTagsList.size(); i++) {
            softAssert.isEquals(productTagsList.get(i).getLmCode(), lmCodes[i], "lmCode");
        }
        softAssert.verifyAll();
        return this;
    }
}
