package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.work.ruptures.data.RuptureData;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.DeleteSessionModalPage;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.ExitActiveSessionModalPage;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.FinishSessionAcceptModalPage;
import com.leroy.magmobile.ui.pages.work.ruptures.widgets.RuptureWidget;
import io.qameta.allure.Step;

import java.util.List;

public class ActiveSessionPage extends SessionPage {
    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button\"]/../following-sibling::android.view.ViewGroup[1]")
    Button deleteBtn;

    @AppFindBy(text = "ПЕРЕБОЙ")
    Button addRuptureBtn;

    @AppFindBy(text = "ЗАВЕРШИТЬ")
    Button endSessionBtn;

    @AppFindBy(text = "Создана заявка на отзыв")
    Element recallRequestHasBeenCreatedMsgLbl;

    AndroidScrollView<RuptureData> ruptureCardScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, "./*/android.view.ViewGroup[android.view.ViewGroup]/descendant::*[3]",
            RuptureWidget.class);

    @Step("Получить список товаровв")
    public List<RuptureData> getRupturesList() throws Exception {
        return ruptureCardScrollView.getFullDataList();
    }

    @Step("Завершить сессию")
    public FinishSessionAcceptModalPage finishSession() {
        endSessionBtn.click();
        return new FinishSessionAcceptModalPage();
    }

    @Step("Нажать на кнопку назад")
    public ExitActiveSessionModalPage exitActiveSession() {
        backBtn.click();
        return new ExitActiveSessionModalPage();
    }

    @Step("Нажать на '+ Перебои'")
    public RupturesScannerPage clickAddRuptureButton() {
        addRuptureBtn.click();
        return new RupturesScannerPage();
    }

    @Step("Нажать по чекбоксу действия {action} у товара {lmCode}")
    public ActiveSessionPage checkRuptureActionCheckBox(String lmCode, String action) throws Exception {
        Element el = E(String.format("//android.widget.TextView[@content-desc='lmCode' and contains(@text,'%s')]/.." +
                "/following-sibling::android.view.ViewGroup[@content-desc='Button-container' and " +
                "descendant::android.widget.TextView[@text='%s']]//android.view.ViewGroup[@content-desc='lmui-Icon']", lmCode, action));
        if (!el.isVisible())
            ruptureCardScrollView.scrollDownToElement(el);
        el.click();
        return this;
    }

    @Step("Удалить сессию")
    public DeleteSessionModalPage deleteSession() {
        deleteBtn.click();
        return new DeleteSessionModalPage();
    }

    @Step("Открыть перебой {ruptureLm}")
    public RuptureCardPage goToRuptureCard(String ruptureLm) throws Exception {
        ruptureCardScrollView.scrollToBeginning();
        Element target = E(String.format("contains(%s)", ruptureLm));
        if (!target.isVisible()) {
            ruptureCardScrollView.scrollDownToElement(target);
        }
        //из-за кнопок "+перебой и завершить"
        //ruptureCardScrollView.scrollDown();
        target.click();
        return new RuptureCardPage();
    }

    @Step("Проверить, что отображается сообщение о созданной заявке на отзыв с RM")
    public ActiveSessionPage shouldRecallRequestHasBeenCreatedMsgIsVisible() {
        anAssert.isElementVisible(recallRequestHasBeenCreatedMsgLbl);
        return this;
    }

    @Step("Проверить, что перебоя нет в списке")
    public ActiveSessionPage shouldRuptureIsNotInList(String lmCode) throws Exception {
        if (!ruptureCardScrollView.isVisible()) {
            return this;
        } else {
            List<RuptureData> uiRuptureDataList = ruptureCardScrollView.getFullDataList();
            for (int i = 0; i < uiRuptureDataList.size(); i++) {
                anAssert.isFalse(uiRuptureDataList.get(i).getLmCode().contains(lmCode), "rupture " + lmCode + " is in the list");
            }
        }
        return this;
    }

    @Step("Проверить, что перебой есть в списке")
    public ActiveSessionPage shouldRuptureInTheList(String lmCode) throws Exception {
        if (!ruptureCardScrollView.isVisible()) {
            return this;
        } else {
            List<RuptureData> uiRuptureDataList = ruptureCardScrollView.getFullDataList();
            for (int i = 0; i < uiRuptureDataList.size(); i++) {
                anAssert.isTrue(uiRuptureDataList.get(i).getLmCode().contains(lmCode), "rupture " + lmCode + " is in the list");
            }
        }
        return this;
    }

    @Step("Проверить, что данные перебоев отображены корректно")
    public ActiveSessionPage shouldRupturesDataIsCorrect(RuptureData... dataArray) throws Exception {
        List<RuptureData> uiRuptureDataList = ruptureCardScrollView.getFullDataList();
        anAssert.isEquals(uiRuptureDataList.size(), dataArray.length, "Разное кол-во данных о перебоях");
        for (int i = 0; i < dataArray.length; i++) {
            anAssert.isEquals(uiRuptureDataList.get(i), dataArray[i], (i + 1) + " данные отличаются");
        }
        return this;
    }

    @Step("Проверить, что данные перебоев отображены корректно")
    public ActiveSessionPage shouldRupturesDataIsCorrect(List<RuptureData> dataList) throws Exception {
        RuptureData[] dataArray = new RuptureData[dataList.size()];
        return shouldRupturesDataIsCorrect(dataList.toArray(dataArray));
    }

    @Step("Проверить, что данные кол-во перебоев корректно")
    public ActiveSessionPage shouldRuptureQuantityIsCorrect(int ruptureQuantity) throws Exception {
        List<RuptureData> uiRuptureDataList = ruptureCardScrollView.getFullDataList();
        anAssert.isEquals(uiRuptureDataList.size(), ruptureQuantity, "Wrong rupture quantity");
        return this;
    }

    @Override
    public void verifyRequiredElements() {
        super.verifyRequiredElements();
        softAssert.areElementsVisible(deleteBtn, addRuptureBtn, endSessionBtn);
        softAssert.verifyAll();
    }
}
