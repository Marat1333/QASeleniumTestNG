package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import io.qameta.allure.Step;

public abstract class SuccessPage extends CommonMagMobilePage {

    protected abstract String getExpectedMainBodyMessage();
    protected abstract String getExpectedSubBodyMessage();
    protected abstract String getExpectedSubmitText();

    @AppFindBy(xpath = "//android.widget.TextView[1]", metaName = "Основной текст экрана")
    Element mainBodyMessage;

    @AppFindBy(xpath = "//android.widget.TextView[2]", metaName = "Дополнительный текст")
    Element subMessage;

    @AppFindBy(xpath = "//*[contains(@content-desc, 'Button')]", metaName = "Кнопка 'Перейти в ...'")
    private MagMobButton submitBtn;

    protected Element getMainBodyMessage() {
        return mainBodyMessage;
    }

    protected Element getSubMessage() {
        return subMessage;
    }

    protected MagMobButton getSubmitBtn() {
        return submitBtn;
    }

    @Override
    protected void waitForPageIsLoaded() {
        try {
            getSubmitBtn().waitUntilTextIsEqualTo(getExpectedSubmitText(), timeout);
        } catch (Exception err) {
            Log.error(err.getMessage());
        }
    }

    // Verifications

    @Step("Проверить, что Экран успеха отображается корректно")
    public SuccessPage verifyRequiredElements() {
        String ps = getPageSource();
        softAssert.areElementsVisible(ps, mainBodyMessage, subMessage, submitBtn);
        softAssert.isElementTextEqual(mainBodyMessage, getExpectedMainBodyMessage(), ps);
        softAssert.isElementTextEqual(subMessage, getExpectedSubBodyMessage(), ps);
        softAssert.isElementTextEqual(submitBtn, getExpectedSubmitText(), ps);
        softAssert.verifyAll();
        return this;
    }

}

