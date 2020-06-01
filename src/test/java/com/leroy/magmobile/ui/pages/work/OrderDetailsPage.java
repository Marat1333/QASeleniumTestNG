package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.models.work.WithdrawalProductCardData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.widgets.ProductCardWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class OrderDetailsPage extends CommonMagMobilePage {

    // Parameters areas
    @AppFindBy(xpath = "//android.widget.TextView[@text='Способ пополнения']/following::android.widget.TextView[1]",
            metaName = "Способ пополнения")
    private Element replenishmentMethod;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Дата доставки товара']/following::android.widget.TextView[1]",
            metaName = "Дата доставки товара")
    private Element deliveryDate;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Ожидаемое время доставки товара']/following::android.widget.TextView[1]",
            metaName = "Ожидаемое время доставки товара")
    private Element deliveryTime;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Комментарий']/following::android.widget.TextView[1]",
            metaName = "Комментарий")
    private Element comment;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='ТОВАРЫ НА ОТЗЫВ']]/following-sibling::android.view.ViewGroup/android.view.ViewGroup",
            clazz = ProductCardWidget.class)
    private ElementList<ProductCardWidget> productsForWithdrawal;

    @Override
    public void waitForPageIsLoaded() {
        replenishmentMethod.waitForVisibility();
        deliveryDate.waitForVisibility();
        comment.waitForVisibility();
    }

    /* ---------------------- Verifications -------------------------- */

    @Step("Проверить, что форма заполнена необходимым образом")
    public OrderDetailsPage shouldFormDataIs(String replenishmentMethod, LocalDate deliveryDate,
                                             LocalTime deliveryTime, String comment) {
        softAssert.isElementTextEqual(this.replenishmentMethod, replenishmentMethod);
        softAssert.isElementTextEqual(this.deliveryDate,
                deliveryDate.format(DateTimeFormatter.ofPattern(
                        "d-го MMM", new Locale("ru"))));
        softAssert.isElementTextEqual(this.deliveryTime,
                deliveryTime.format(DateTimeFormatter.ofPattern("HH:mm"))); // TODO
        softAssert.isElementTextEqual(this.comment, comment);
        softAssert.verifyAll();
        return this;
    }

    @Step("{index}-ий товар должен соответствовать: (expectedCardData)")
    public OrderDetailsPage shouldProductByIndexIs(
            int index, WithdrawalProductCardData expectedCardData) throws Exception {
        index--;
        expectedCardData.setAvailableQuantity(null);
        ProductCardWidget cardObj = productsForWithdrawal.get(index);
        WithdrawalProductCardData actualCardData = new WithdrawalProductCardData();
        actualCardData.setLmCode(cardObj.getNumber());
        actualCardData.setTitle(cardObj.getName());
        actualCardData.setPriceUnit(cardObj.getQuantityUnit());
        actualCardData.setSelectedQuantity(ParserUtil.strToDouble(cardObj.getQuantity()));
        actualCardData.assertEqualsNotNullExpectedFields(index, expectedCardData);
        return this;
    }
}
