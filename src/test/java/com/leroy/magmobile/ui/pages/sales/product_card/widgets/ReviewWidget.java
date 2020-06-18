package com.leroy.magmobile.ui.pages.sales.product_card.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.product_card.ReviewCardData;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import org.openqa.selenium.WebDriver;

public class ReviewWidget extends CardWidget<ReviewCardData> {
    public ReviewWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./android.widget.TextView[1]/following-sibling::android.widget.TextView[contains(@text, 'Из города')]" +
            "/preceding-sibling::android.widget.TextView[not(contains(@text,'Подтвержденная покупка'))][2]")
    Element nameLbl;

    @AppFindBy(xpath = "./android.widget.TextView[2]/following-sibling::android.widget.TextView[contains(@text, 'Из города')]" +
            "/preceding-sibling::android.widget.TextView[not(contains(@text,'Подтвержденная покупка'))][1]")
    Element dateLbl;

    @AppFindBy(xpath = "./android.widget.TextView[contains(@text, 'Из города:')]/following-sibling::android.widget.TextView[1]")
    Element cityLbl;

    @AppFindBy(xpath = "./android.widget.TextView[contains(@text, 'Из города:')]/following-sibling::android.widget.TextView[2]")
    Element reviewBodyLbl;

    @AppFindBy(xpath = ".//*[contains(@text, 'Достоинства')]")
    Element advantagesLbl;

    @AppFindBy(xpath = ".//*[contains(@text, 'Недостатки')]")
    Element disadvantagesLbl;

    @AppFindBy(xpath = ".//*[contains(@text, 'Я рекомендую этот товар!')]")
    Element recommendedLbl;

    @Override
    public ReviewCardData collectDataFromPage() {
        return collectDataFromPage(getPageSource());
    }

    @Override
    public ReviewCardData collectDataFromPage(String pageSource) {
        ReviewCardData data = new ReviewCardData();
        data.setName(nameLbl.getText());
        data.setDate(dateLbl.getText());
        data.setCity(cityLbl.getText());
        data.setReviewBody(reviewBodyLbl.getText());
        if (advantagesLbl.isVisible()) {
            data.setAdvantages(advantagesLbl.getText());
        }
        if (disadvantagesLbl.isVisible()) {
            data.setDisadvantages(disadvantagesLbl.getText());
        }
        if (recommendedLbl.isVisible()) {
            data.setRecommendedProduct(true);
        } else {
            data.setRecommendedProduct(false);
        }
        return data;
    }

    @Override
    public boolean isFullyVisible() {
        return isFullyVisible(getPageSource());
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return nameLbl.isVisible() && reviewBodyLbl.isVisible();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        ReviewWidget widget = (ReviewWidget) o;
        return this.reviewBodyLbl.getText().equals(widget.reviewBodyLbl.getText())
                && this.dateLbl.getText().equals(widget.dateLbl.getText());
    }
}
