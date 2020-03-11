package com.leroy.magmobile.ui.pages.support;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SupportPage extends CommonMagMobilePage {

    public static final String COMPLAIN_REQUEST = "пожаловатсья";
    public static final String ASK_QUESTION_REQUEST = "задать вопрос";

    public SupportPage(Context context) {
        super(context);
    }

    // Top Area with title and 2 buttons
    @AppFindBy(containsText = "Дорогая служба поддержки")
    private Element titleLbl;

    @AppFindBy(text = "пожаловаться")
    private MagMobGreenSubmitButton complainBtn;

    @AppFindBy(text = "задать вопрос")
    private MagMobGreenSubmitButton askQuestionBtn;

    // Main area

    @AppFindBy(xpath = "//android.widget.ScrollView//android.widget.TextView")
    private ElementList<Element> mainLabels;

    @Override
    public void waitForPageIsLoaded() {
        titleLbl.waitForVisibility();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажмите на плашку {button}")
    public ComplainPage clickButton(String button) {
        E(button).click();
        return new ComplainPage(context);
    }

    /* ---------------------- Verifications -------------------------- */

    @Step("Проверить, что страница 'Поддержка' отображается корректно")
    public SupportPage verifyRequiredElements() throws Exception {
        softAssert.isElementVisible(titleLbl);
        softAssert.isElementVisible(complainBtn);
        softAssert.isElementVisible(askQuestionBtn);
        softAssert.isTrue(mainLabels.getCount() > 0, "Плашки с видами жалоб не отображаются");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбран тип запроса - {request}")
    public SupportPage shouldSelectedTypeRequestIs(String request) {
        switch (request) {
            case COMPLAIN_REQUEST:
                anAssert.isTrue(complainBtn.isEnabled(),
                        "Кнопка 'пожаловаться' должна быть активна");
                break;
            case ASK_QUESTION_REQUEST:
                anAssert.isTrue(askQuestionBtn.isEnabled(),
                        "Кнопка 'задать вопрос' должна быть активна");
                break;
            default:
                Assert.fail("Неизвестный тип запроса");
        }
        return this;
    }
}
