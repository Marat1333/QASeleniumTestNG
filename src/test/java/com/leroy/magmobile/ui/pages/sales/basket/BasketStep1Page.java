package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.sales.ProductCardPage;
import com.leroy.magmobile.ui.pages.sales.widget.SearchProductCardWidget;
import io.qameta.allure.Step;

public class BasketStep1Page extends BasketPage {

    @AppFindBy(xpath = "//android.view.ViewGroup[android.view.ViewGroup[@content-desc='lmCode']]")
    private SearchProductCardWidget productCard;

    @AppFindBy(text = "Услуги моего отдела")
    private MagMobButton servicesMyDepartmentArea;

    @AppFindBy(text = "Итого: ")
    Element totalPriceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/following-sibling::android.widget.TextView")
    Element totalPriceVal;

    @AppFindBy(text = "ТОВАР")
    private MagMobButton addProductBtn;

    @AppFindBy(text = "ДАЛЕЕ К ПАРАМЕТРАМ")
    private MagMobGreenSubmitButton nextParametersBtn;

    @Override
    public void waitForPageIsLoaded() {
        servicesMyDepartmentArea.waitForVisibility();
    }

    // --------- Action steps -------------------//

    @Step("Нажмите Далее к параметрам")
    public BasketStep2Page clickNextParametersButton() {
        nextParametersBtn.click();
        return new BasketStep2Page();
    }

    @Step("Нажмите кнопку назад в верхнем меню")
    public ProductCardPage clickBackButton() {
        backBtn.click();
        return new ProductCardPage();
    }

    // --------- Verifications ------------------//

    @Override
    @Step("Убедиться, что мы находимся на странице Корзина - Шаг 1, и все необходимые элементы отражаются корректно")
    public BasketStep1Page verifyRequiredElements() {
        super.verifyRequiredElements();
        softAssert.areElementsVisible(
                productCard, servicesMyDepartmentArea, totalPriceLbl,
                totalPriceVal, addProductBtn, nextParametersBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что номер документа в корзине равен {number}")
    public BasketStep1Page shouldDocumentNumberIs(String number) {
        anAssert.isEquals(getDocumentNumber(), number, "Неверный номер документа");
        return this;
    }

    @Step("Проверить, что ЛМ код товара в козрине равен {number}")
    public BasketStep1Page shouldLmCodeOfProductIs(String number) {
        anAssert.isEquals(productCard.getLmCode(true), number, "Неверный ЛМ код товара в корзине");
        return this;
    }

}

