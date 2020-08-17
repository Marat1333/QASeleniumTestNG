package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.ruptures.data.RuptureData;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.ExitActiveSessionModalPage;
import com.leroy.magmobile.ui.pages.work.ruptures.widgets.RuptureWidget;
import io.qameta.allure.Step;

import java.util.List;

public class SessionProductsListPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "Button")
    Button backBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button\"]/../following-sibling::android.view.ViewGroup[1]")
    Button deleteBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button\"]/../following-sibling::android.widget.TextView[1]")
    Element creationDateLbl;

    @AppFindBy(containsText = " перебо")
    Element creatorAndRuptureQuantityLbl;

    @AppFindBy(containsText = "Сессия №")
    Element sessionNumberAndStatusLbl;

    @AppFindBy(text = "ПЕРЕБОЙ")
    Button addRuptureBtn;

    @AppFindBy(text = "ЗАВЕРШИТЬ")
    Button endSessionBtn;

    AndroidScrollView<RuptureData> ruptureCardScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, "./*/android.view.ViewGroup[android.view.ViewGroup]/descendant::*[3]",
            RuptureWidget.class);

    @Override
    protected void waitForPageIsLoaded() {
        creationDateLbl.waitForVisibility();
        sessionNumberAndStatusLbl.waitForVisibility();
    }

    @Step("Нажать на кнопку назад")
    public ExitActiveSessionModalPage exitActiveSession() {
        backBtn.click();
        return new ExitActiveSessionModalPage();
    }

    @Step("Проверить, что данные перебоев отображены корректно")
    public SessionProductsListPage shouldRupturesDataIsCorrect(RuptureData... dataArray) throws Exception {
        List<RuptureData> uiRuptureDataList = ruptureCardScrollView.getFullDataList();
        for (int i = 0; i < dataArray.length; i++) {
            anAssert.isEquals(uiRuptureDataList.get(i), dataArray[i], "data mismatch");
        }
        return this;
    }

    public SessionProductsListPage verifyRequiredElements() {
        softAssert.areElementsVisible(backBtn, deleteBtn, creationDateLbl, creatorAndRuptureQuantityLbl,
                sessionNumberAndStatusLbl, addRuptureBtn, endSessionBtn);
        softAssert.verifyAll();
        return this;
    }
}
