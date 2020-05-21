package com.leroy.magmobile.ui.pages.sales.estimate;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import io.qameta.allure.Step;

public class SubmittedSendEmailPage extends CommonMagMobilePage {

    public SubmittedSendEmailPage(Context context) {
        super(context);
    }

    @AppFindBy(containsText = "Смета отправлена", metaName = "Основное сообщение страницы (выжеделно жирным)")
    Element header;

    @AppFindBy(text = "ПЕРЕЙТИ В СПИСОК ДОКУМЕНТОВ", metaName = "Кнопка 'ПЕРЕЙТИ В СПИСОК ДОКУМЕНТОВ'")
    MagMobButton submitBtn;

    // Actions

    @Step("Нажать кнопку 'Перейти в список документов'")
    public SalesDocumentsPage clickSubmitButton() {
        submitBtn.click();
        return new SalesDocumentsPage(context);
    }

    // Verifications

    @Step("Проверить, что страница об успешной отправке email отображается корректно")
    public SubmittedSendEmailPage verifyRequiredElements() {
        softAssert.areElementsVisible(header, submitBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что смета отправлена на почту {email}")
    public SubmittedSendEmailPage shouldSendToThisEmail(String email) {
        anAssert.isEquals(header.getText(), "Смета отправлена на email\n" + email,
                "Ожидалась другая почта");
        return this;
    }
}
