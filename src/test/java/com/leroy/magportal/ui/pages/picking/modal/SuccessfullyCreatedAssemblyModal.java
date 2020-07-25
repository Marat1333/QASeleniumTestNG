package com.leroy.magportal.ui.pages.picking.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.pages.picking.PickingContentPage;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;

public class SuccessfullyCreatedAssemblyModal extends MagPortalBasePage {

    protected final static String MODAL_DIV_XPATH = "//div[contains(@class, 'Common-ConfirmModal__modal__container')]";

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'ConfirmModal__modal__body')]//span",
            metaName = "Основной текст")
    Element mainBodyMsg;

    @WebFindBy(id = "navigateToNewTaskBtn",
            metaName = "Кнопка 'Перейти к новому Заданию на Сборку'")
    Button navigateToNewTaskBtn;

    @WebFindBy(id = "remainOldTaskBtn",
            metaName = "Кнопка 'Вернуться к Оригинальной сборке'")
    Button remainOldTaskBtn;

    @Override
    protected void waitForPageIsLoaded() {
        mainBodyMsg.waitForVisibility();
    }

    // Grab data

    @Step("Получить номер сборки")
    public String getAssemblyNumber() {
        String bodyText = mainBodyMsg.getText();
        String orderNumber = StringUtils.substringBetween(bodyText, "заказа", "создана").trim();
        String assemblyShortNumber = StringUtils.substringBetween(bodyText, "сборка", "заказа ").trim();
        return assemblyShortNumber.replaceAll("\\*", "") + " " + orderNumber;
    }

    // Actions

    @Step("Нажать кнопку 'Перейти к новому Заданию на сборку'")
    public PickingContentPage clickNavigateToNewTaskButton() {
        navigateToNewTaskBtn.click();
        waitForSpinnerAppearAndDisappear();
        return new PickingContentPage();
    }

    @Step("Нажать кнопку 'Вернуться к Оригинальной сборке'")
    public PickingContentPage clickRemainOldTaskButton() {
        remainOldTaskBtn.click();
        waitForSpinnerAppearAndDisappear();
        return new PickingContentPage();
    }

    // Verifications

    @Step("Проверить, что модальное окно 'Об успешном создании новой сборки' отображается корректно")
    public SuccessfullyCreatedAssemblyModal verifyRequiredElements() {
        softAssert.areElementsVisible(mainBodyMsg, navigateToNewTaskBtn, remainOldTaskBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что номер заказа должен быть {value}")
    public SuccessfullyCreatedAssemblyModal shouldOrderNumberIs(String value) {
        if (!value.startsWith("*"))
            value = "*" + value;
        anAssert.isEquals(StringUtils.substringBetween(mainBodyMsg.getText(), "заказа", "создана").trim(),
                value, "Ожидался другой (короткий) номер заказа");
        return this;
    }

}
