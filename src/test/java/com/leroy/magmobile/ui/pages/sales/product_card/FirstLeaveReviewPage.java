package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.api.enums.ReviewOptions;
import com.leroy.magmobile.ui.elements.MagMobCheckBox;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.PeriodOfUsageModalPage;
import com.leroy.utils.ParserUtil;
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

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Цена')]/following-sibling::*[1]")
    Element currentPriceRateLbl;

    @AppFindBy(text = "Качество")
    Element qualityLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Качество')]/following-sibling::*[1]")
    Element currentQualityRateLbl;

    @AppFindBy(text = "не выбрано")
    ElementList<Element> notChosenLbls;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Общая оценка')]/following-sibling::android.widget.TextView")
    Element commonStringRate;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Я рекомендую этот товар')]/following-sibling::*//*[@content-desc='Button']")
    MagMobCheckBox recommendedProductCheckBox;

    @AppFindBy(accessibilityId = "timeUsage")
    Button callPeriodOfUsageModalBtn;

    @AppFindBy(text = "ДАЛЕЕ")
    Button moveToNextWindowBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView", metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    public enum PriceAndQuantityRate {
        NOT_CHOSEN("не выбрано"),
        TERRIBLE("ужасно"),
        BAD("плохо"),
        SATISFACTORILY("удовлетворительно"),
        GOOD("хорошо"),
        PERFECT("отлично");

        private String name;

        PriceAndQuantityRate(String name){
            this.name=name;
        }

        public String getName() {
            return name;
        }
    }

    public enum CommonRate{
        NOT_CHOSEN("Нажми, чтобы оценить"),
        TERRIBLE("Ужасный товар"),
        BAD("Плохой товар"),
        SATISFACTORILY("Обычный товар"),
        GOOD("Хороший товар"),
        PERFECT("Отличный товар");

        private String name;

        CommonRate(String name){
            this.name=name;
        }

        public String getName() {
            return name;
        }
    }

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

    @Step("Проверить, что лм код подтянулся корректно")
    public FirstLeaveReviewPage shouldLmCodeIsCorrect(String lmCode){
        anAssert.isEquals(ParserUtil.strWithOnlyDigits(productLmCodeLbl.getText()), lmCode, "lmCode");
        return this;
    }

    @Step("Проверить, что оценки корректно проставлены")
    public FirstLeaveReviewPage shouldRatesIsCorrect(CommonRate commonRate, PriceAndQuantityRate priceRate, PriceAndQuantityRate quantityRate) throws Exception{
        anAssert.isElementTextEqual(commonStringRate, commonRate.getName());
        if (commonRate.equals(CommonRate.GOOD)||commonRate.equals(CommonRate.PERFECT)){
            anAssert.isTrue(recommendedProductCheckBox.isChecked(), "Чек-бокс \"Я рекомендую этот товар\" не выбран");
        }else {
            anAssert.isTrue(!recommendedProductCheckBox.isChecked(), "Чек-бокс \"Я рекомендую этот товар\" выбран");
        }
        anAssert.isElementTextEqual(currentPriceRateLbl, priceRate.getName());
        anAssert.isElementTextEqual(currentQualityRateLbl, quantityRate.getName());
        return this;
    }

    @Step("Проверить, что в поле \"Срок использования\" указано корректное значение")
    public FirstLeaveReviewPage shouldPeriodOfUsageIsCorrect(ReviewOptions option){
        if (option.equals(ReviewOptions.DEFAULT)){
            //
        }
        anAssert.isElementTextEqual(callPeriodOfUsageModalBtn, option.getName());
        return this;
    }

}
