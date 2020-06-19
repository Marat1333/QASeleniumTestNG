package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobCheckBox;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.PeriodOfUsageModalPage;
import io.qameta.allure.Step;

import java.util.NoSuchElementException;

public class FirstLeaveReviewPage extends CommonMagMobilePage {
    @AppFindBy(text = "Новый отзыв")
    Element headerLbl;

    @AppFindBy(accessibilityId = "lmCode")
    Element productLmCodeLbl;

    @AppFindBy(text = "Общая оценка")
    Element overallRatingLbl;

    @AppFindBy(text = "Цена")
    Element priceLbl;

    @AppFindBy(text = "Качество")
    Element qualityLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Я рекомендую этот товар')]/following-sibling::*")
    MagMobCheckBox recommendedProductCheckBox;

    @AppFindBy(text = "Срок использования")
    Button callPeriodOfUsageModalBtn;

    @AppFindBy(text = "ДАЛЕЕ")
    Button moveToNextWindowBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView", metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
        productLmCodeLbl.waitForVisibility();
    }

    @Step("Оценить продукт: цена - {price}, качество - {quality}, общая оценка - {rate}")
    public FirstLeaveReviewPage makeRates(int price, int quality, int rate) throws Exception{
        rateProduct(rate);
        ratePrice(price);
        rateQuality(quality);
        return this;
    }

    @Step("Поставить общую оценку продукту от 1 до 5")
    private FirstLeaveReviewPage rateProduct(int rate)throws Exception{
        try {
            overallRatingLbl.findChildElement("./following-sibling::android.view.ViewGroup["+rate+"]").click();
        }catch (NoSuchElementException e){
            throw new IllegalArgumentException("rate should be in [1-5]");
        }
        return this;
    }

    @Step("Оценить цену продукта от 1 до 5")
    private FirstLeaveReviewPage ratePrice(int rate) throws Exception{
        try {
            priceLbl.findChildElement("./following-sibling::android.view.ViewGroup["+rate+"]").click();
        }catch (NoSuchElementException e){
            throw new IllegalArgumentException("rate should be in [1-5]");
        }
        return this;
    }

    @Step("Оценить качество продукта от 1 до 5")
    private FirstLeaveReviewPage rateQuality(int rate) throws Exception{
        try {
            qualityLbl.findChildElement("./following-sibling::android.view.ViewGroup["+rate+"]").click();
        }catch (NoSuchElementException e){
            throw new IllegalArgumentException("rate should be in [1-5]");
        }
        return this;
    }

    @Step("Нажать на чек-бокс \"Я рекомендую этот товар\"")
    public FirstLeaveReviewPage leaveRecommendation(){
        recommendedProductCheckBox.click();
        return this;
    }

    @Step("Открыть модальное окно времени использования")
    public PeriodOfUsageModalPage callPeriodOfUsageModal(){
        mainScrollView.scrollToEnd();
        callPeriodOfUsageModalBtn.click();
        return new PeriodOfUsageModalPage();
    }

    @Step("Перейти на следующую форму отзыва о продукте")
    public SecondLeaveReviewPage goToNextReviewPage(){
        moveToNextWindowBtn.click();
        return new SecondLeaveReviewPage();
    }

    //TODO recommendation, period of usage, String rates verifications
}
