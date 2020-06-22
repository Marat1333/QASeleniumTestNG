package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.catalog.product.reviews.CatalogReviewsOfProductList;
import com.leroy.magmobile.api.data.catalog.product.reviews.CatalogReviewsOfProductsData;
import com.leroy.magmobile.ui.models.product_card.ReviewCardData;
import com.leroy.magmobile.ui.pages.sales.product_card.widgets.ReviewWidget;
import com.leroy.utils.DateTimeUtil;
import io.qameta.allure.Step;

import java.util.List;

public class ReviewsPage extends ProductCardPage {

    private AndroidScrollView<ReviewCardData> reviewCardsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            "./android.view.ViewGroup/*/*", ReviewWidget.class);

    @AppFindBy(xpath = "//androidx.viewpager.widget.ViewPager/*/*[1]/android.widget.TextView")
    Element reviewCountLbl;

    @AppFindBy(text = "ОСТАВИТЬ ОТЗЫВ")
    Button leaveReview;

    @Override
    public void waitForPageIsLoaded() {
        reviewCountLbl.waitForVisibility();
    }

    @Step("Нажать на кнопку \"Оставить отзыв\"")
    public FirstLeaveReviewPage leaveReview(){
        leaveReview.click();
        return new FirstLeaveReviewPage();
    }

    @Step("Проверить, кол-во отзывов")
    public ReviewsPage shouldReviewsCountIsCorrect(CatalogReviewsOfProductList data){
        String reviewsCount = String.valueOf(data.getTotalCount());
        if (reviewsCount.equals("0")){
            anAssert.isElementTextContains(reviewCountLbl, "Нет отзывов");
        }else {
            anAssert.isElementTextContains(reviewCountLbl, reviewsCount);
        }
        return this;
    }

    @Step("Проверить корректное отображение отзывов")
    public ReviewsPage shouldReviewsAreCorrect(CatalogReviewsOfProductList data){
        List<ReviewCardData> reviewCardDataList = reviewCardsScrollView.getFullDataList();
        List<CatalogReviewsOfProductsData> dataList = data.getItems();
        ReviewCardData tmpFrontData;
        CatalogReviewsOfProductsData tmpApiData;
        for (int i=0; i<dataList.size();i++){
            tmpFrontData = reviewCardDataList.get(i);
            tmpApiData = dataList.get(i);
            softAssert.isEquals(tmpApiData.getAuthor().getName(), tmpFrontData.getName(), "name");
            softAssert.isEquals(tmpApiData.getAuthor().getLocation(), tmpFrontData.getCity(), "city");
            softAssert.isEquals(tmpApiData.getBody(), tmpFrontData.getReviewBody(), "review body");
            softAssert.isEquals(DateTimeUtil.strToLocalDate(tmpApiData.getCreated_at(), "yyyy-MM-dd'T'HH:mm:ss.SSS"),
                    DateTimeUtil.strToLocalDate(tmpFrontData.getDate(), "d MMMM yyyy"), "date");
            softAssert.verifyAll();
        }
        return this;
    }

    public void verifyRequiredElements() {
        softAssert.areElementsVisible(leaveReview, reviewCountLbl);
        softAssert.verifyAll();
    }
}
