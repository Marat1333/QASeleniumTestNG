package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.models.ProductCardData;
import com.leroy.magmobile.ui.pages.widgets.ProductCardWidget;
import io.qameta.allure.Step;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class OrderDetailsPage extends CommonMagMobilePage {

    public OrderDetailsPage(TestContext context) {
        super(context);
    }

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
                        "dd-го MMM", new Locale("ru"))));
        softAssert.isElementTextEqual(this.deliveryTime,
                deliveryTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        softAssert.isElementTextEqual(this.comment, comment);
        softAssert.verifyAll();
        return this;
    }

    @Step("{index}-ий товар должен соответствовать: {productCardData}")
    public OrderDetailsPage shouldProductByIndexIs(
            int index, ProductCardData productCardData) throws Exception {
        index--;
        ProductCardWidget productCardWidget = productsForWithdrawal.get(index);
        softAssert.isEquals(productCardWidget.getNumber(), productCardData.getLmCode(),
                "Номер товара на отзыв должен быть %s");
        softAssert.isEquals(productCardWidget.getName(), productCardData.getName(),
                "Название товара на отзыв должно быть %s");
        softAssert.isEquals(productCardWidget.getQuantity(), productCardData.getSelectedQuantity(),
                "Кол-во товара на отзыв должно быть %s");
        softAssert.isEquals(productCardWidget.getQuantityType(), productCardData.getQuantityType(),
                "Тип кол-ва должен быть %s");
        softAssert.verifyAll();
        return this;
    }
}
