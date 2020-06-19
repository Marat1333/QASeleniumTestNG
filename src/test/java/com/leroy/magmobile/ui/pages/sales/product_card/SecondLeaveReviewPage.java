package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class SecondLeaveReviewPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "comment")
    EditBox commentInput;

    @AppFindBy(accessibilityId = "pros")
    EditBox advantagesInput;

    @AppFindBy(accessibilityId = "cons")
    EditBox disadvantagesInput;

    @AppFindBy(text = "ОТПРАВИТЬ")
    Button sendBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView", metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    @Step("Заполнить поля: комментарий - {comment}, достоинства - {advantage}, недостатки - {disadvantage}")
    public SecondLeaveReviewPage fillAllFields(String comment, String advantage, String disadvantage){
        leaveComment(comment);
        leaveAdvantage(advantage);
        leaveDisadvantage(disadvantage);
        return this;
    }

    @Step("Оставить комментарией")
    private SecondLeaveReviewPage leaveComment(String comment){
        commentInput.click();
        commentInput.waitForInvisibility();
        mainScrollView.scrollToBeginning();
        commentInput.clearAndFill(comment);
        return this;
    }

    @Step("Описать достоинства")
    private SecondLeaveReviewPage leaveAdvantage(String advantage){
        advantagesInput.click();
        advantagesInput.clearAndFill(advantage);
        return this;
    }

    @Step("Описать недостатки")
    private SecondLeaveReviewPage leaveDisadvantage(String disadvantage){
        disadvantagesInput.click();
        mainScrollView.scrollToEnd();
        disadvantagesInput.clearAndFill(disadvantage);
        return this;
    }

    @Step("Отправить отзыв")
    public SuccessReviewSendingPage sendReview(){
        sendBtn.click();
        return new SuccessReviewSendingPage();
    }
}
