package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.models.salesdoc.EstimatePuzData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardPuzData;
import com.leroy.magportal.ui.pages.cart_estimate.modal.SubmittedEstimateModal;
import com.leroy.magportal.ui.pages.cart_estimate.widget.ProductOrderCardPuzWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.Converter;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

public class CreateEstimatePage extends CreateCartEstimatePage {

    public CreateEstimatePage(TestContext context) {
        super(context);
    }

    @WebFindBy(xpath = "//div[contains(@class, 'Estimate-EstimatesView__header-text')]", metaName = "Номер сметы")
    Element estimateNumber;

    @WebFindBy(xpath = "//div[contains(@class, 'Estimate-EstimatesView__cart')]/div[contains(@class, 'SalesDocProduct')]",
            clazz = ProductOrderCardPuzWidget.class)
    CardWebWidgetList<ProductOrderCardPuzWidget, ProductOrderCardPuzData> products;

    @Override
    public ElementList<ProductOrderCardPuzWidget> products() {
        return products;
    }


    @WebFindBy(xpath = "//button[descendant::span[text()='Создать']]", metaName = "Кнопка 'Создать'")
    Element createBtn;

    private final String FOOTER_INFO_XPATH = "//div[contains(@class, 'SalesDoc-ViewFooter__info')]";

    @WebFindBy(xpath = FOOTER_INFO_XPATH + "/span[1]",
            metaName = "Информация о кол-ве товаров и их общем весе")
    Element countAndWeightProductLbl;

    @WebFindBy(xpath = FOOTER_INFO_XPATH + "/div/span[1]", metaName = "Значение суммы итого")
    Element totalPriceValue;

    @Override
    public void waitForPageIsLoaded() {
        searchProductFld.waitForVisibility();
    }

    // Grab information from page

    @Step("Получить номер сметы со страницы")
    public String getEstimateNumber() {
        return Converter.strToStrWithoutDigits(estimateNumber.getText());
    }

    @Step("Получить информацию о добавленных в смету продуктах со страницы")
    public List<ProductOrderCardPuzData> getEstimateProducts() {
        return products.getDataList();
    }

    @Step("Получить информацию о смете со страницы")
    public EstimatePuzData getEstimateData() {
        EstimatePuzData estimateData = new EstimatePuzData();
        estimateData.setProductCardDataList(products.getDataList());

        String[] productCountAndWeight = countAndWeightProductLbl.getText().split("•");
        anAssert.isTrue(productCountAndWeight.length == 2,
                "Что-то изменилось в метке содержащей информацию о кол-ве и весе товара");

        estimateData.setTotalPrice(Converter.strToDouble(totalPriceValue.getText()));
        estimateData.setTotalWeight(Converter.strToDouble(productCountAndWeight[1]));
        estimateData.setProductCount(Converter.strToInt(productCountAndWeight[0]));

        return estimateData;
    }

    // Actions

    @Step("Нажимаем кнопку 'Создать'")
    public SubmittedEstimateModal clickCreateButton() {
        createBtn.click();
        return new SubmittedEstimateModal(context);
    }

    @Step("Скопировать товар #{index}")
    public CreateEstimatePage copyProductByIndex(int index) throws Exception {
        index--;
        products.get(index).clickCopy();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    // Verifications

    @Step("Проверить, что страница 'Создания сметы' отображается корректно")
    public CreateEstimatePage verifyRequiredElements() {
        softAssert.areElementsVisible(createBtn, addCustomerBtnLbl, searchProductFld);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что смета еще не создана (номер не отображается")
    public CreateEstimatePage shouldEstimateDoesNotHaveNumber() {
        anAssert.isEquals(getEstimateNumber(), "", "Смета имеет номер");
        return this;
    }

    @Step("Проверить, что в Смете добавлены товары с ЛМ кодами: {lmCodes}")
    public CreateEstimatePage shouldEstimateHasProducts(List<String> lmCodes) {
        List<String> actualLmCodes = new ArrayList<>();
        for (ProductOrderCardPuzWidget widget : products) {
            actualLmCodes.add(widget.getLmCode());
        }
        anAssert.isEquals(actualLmCodes, lmCodes, "Ожидались другие товары в смете");
        return this;
    }

    @Step("Проверить, что на странице сметы содержатся ожидаемые данные")
    public CreateEstimatePage shouldEstimateHasData(EstimatePuzData expectedEstimateData) {
        EstimatePuzData actualEstimateData = getEstimateData();
        anAssert.isEquals(actualEstimateData.getProductCardDataList().size(),
                expectedEstimateData.getProductCardDataList().size(),
                "Ожидалось другое кол-во товаров в смете");
        for (int i = 0; i < actualEstimateData.getProductCardDataList().size(); i++) {
            ProductOrderCardPuzData actualProduct = actualEstimateData.getProductCardDataList().get(i);
            ProductOrderCardPuzData expectedProduct = expectedEstimateData.getProductCardDataList().get(i);
            if (expectedProduct.getSelectedQuantity() != null)
                softAssert.isEquals(actualProduct.getSelectedQuantity(), expectedProduct.getSelectedQuantity(),
                        "Товар #" + i + " - ожидался другое кол-во");
            if (expectedProduct.getTotalPrice() != null)
                softAssert.isEquals(actualProduct.getTotalPrice(), expectedProduct.getTotalPrice(),
                        "Товар #" + i + " - ожидалась другая стоимость");
            if (expectedProduct.getAvailableTodayQuantity() != null)
                softAssert.isEquals(actualProduct.getAvailableTodayQuantity(), expectedProduct.getAvailableTodayQuantity(),
                        "Товар #" + i + " - ожидалось другое доступное кол-во");
            else
                softAssert.isTrue(actualProduct.getAvailableTodayQuantity() >= 0,
                        "Товар #" + i + " - ожидалось, что доступное кол-во >= 0");
            if (expectedProduct.getWeight() != null)
                softAssert.isEquals(actualProduct.getWeight(), expectedProduct.getWeight(),
                        "Товар #" + i + " - ожидался другой вес");
            else
                softAssert.isTrue(actualProduct.getWeight() > 0,
                        "Товар #" + i + " - ожидался вес > 0");
        }

        softAssert.isEquals(actualEstimateData.getTotalPrice(), expectedEstimateData.getTotalPrice(),
                "Неверное сумма итого");
        if (expectedEstimateData.getTotalWeight() != null)
            softAssert.isEquals(actualEstimateData.getTotalWeight(), expectedEstimateData.getTotalWeight(),
                    "Неверный итого вес");
        else
            softAssert.isTrue(actualEstimateData.getTotalWeight() > 0,
                    "Ожидался итого вес > 0");
        softAssert.isEquals(actualEstimateData.getProductCount(), expectedEstimateData.getProductCount(),
                "Неверный итого вес");
        softAssert.verifyAll();
        return this;
    }
}
