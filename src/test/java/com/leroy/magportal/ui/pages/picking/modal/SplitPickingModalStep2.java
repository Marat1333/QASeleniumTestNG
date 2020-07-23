package com.leroy.magportal.ui.pages.picking.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.TextArea;
import com.leroy.magportal.ui.webelements.commonelements.PuzCheckBox;
import com.leroy.magportal.ui.webelements.commonelements.PuzSelectControl;
import io.qameta.allure.Step;

public class SplitPickingModalStep2 extends SplitPickingModal {

    @WebFindBy(xpath = "//button[contains(@class, 'SplitModal__switchButton')][1]", metaName = "Опция 'Мой отдел'")
    PuzCheckBox myDepartmentRadioBtn;

    @WebFindBy(xpath = "//button[contains(@class, 'SplitModal__switchButton')][2]", metaName = "Опция 'Я'")
    PuzCheckBox iAmRadioBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Select__container')]", metaName = "Выпадающий список с выбором отдела")
    PuzSelectControl departmentSelector;

    @WebFindBy(id = "textAreaId", metaName = "Поле Комментарий")
    TextArea commentFld;

    @WebFindBy(xpath = "//div[contains(@class, 'SplitModal__footer')]//button", metaName = "Кнопка Создать сборку")
    Button createAssemblyBtn;

    // Actions

    @Step("Заполнить комментарий")
    public SplitPickingModalStep2 enterComment(String text) {
        commentFld.clearFillAndSubmit(text);
        return this;
    }

    @Step("Нажать кнопку Создать сборку")
    public SuccessfullyCreatedAssemblyModal clickCreateAssemblyButton() {
        createAssemblyBtn.click();
        return new SuccessfullyCreatedAssemblyModal();
    }

    // Verifications

    @Step("Проверить, что модальное окно 'Разделение сборки (Шаг 2)' отображается корректно")
    public SplitPickingModalStep2 verifyRequiredElements() {
        softAssert.areElementsVisible(myDepartmentRadioBtn, iAmRadioBtn,
                departmentSelector, commentFld, createAssemblyBtn);
        softAssert.isTrue(createAssemblyBtn.isEnabled(), "Кнопка 'Создать сборку' неактивна");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что ответственный за сборку выбран 'Я'")
    public SplitPickingModalStep2 shouldIamResponsibleForAssemblyOptionSelected() throws Exception{
        anAssert.isTrue(iAmRadioBtn.isChecked(), "Опция 'Я' не выбрана");
        return this;
    }

}
