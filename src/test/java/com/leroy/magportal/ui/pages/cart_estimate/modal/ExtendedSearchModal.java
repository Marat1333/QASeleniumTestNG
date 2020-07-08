package com.leroy.magportal.ui.pages.cart_estimate.modal;

import com.leroy.core.ContextProvider;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.pages.cart_estimate.widget.AddProductsFromSearchWidget;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.List;

public class ExtendedSearchModal extends MagPortalBasePage {

    private static final String MODAL_DIV_XPATH = "//div[contains(@class, 'ExtendedSearchViewModal')]";

    @WebFindBy(xpath = "//div[contains(@class, 'ExtendedSearchViewModal__align-end')]//button",
            metaName = "Кнопка крестик (закрыть)")
    Element closeBtn;

    @WebFindBy(xpath = "//div[span[div[contains(@class, 'BarViewProductCard__container')]]]",
            clazz = AddProductsFromSearchWidget.class)
    CardWebWidgetList<AddProductsFromSearchWidget, ProductOrderCardWebData> productWidgetList;

    public static boolean isModalVisible() {
        return new Element(ContextProvider.getDriver(), By.xpath(MODAL_DIV_XPATH)).isVisible();
    }

    // Grab data
    @Step("Забрать информацию о {index}-ом товаре")
    public ProductOrderCardWebData getProductData(int index) throws Exception {
        index--;
        AddProductsFromSearchWidget productWidget = productWidgetList.get(index);
        return productWidget.collectDataFromPage();
    }

    // Actions

    @Step("Нажать 'Добавить' для {productIndex}-ого товара")
    public ExtendedSearchModal clickAddButton(int productIndex) throws Exception {
        productIndex--;
        AddProductsFromSearchWidget productWidget = productWidgetList.get(productIndex);
        productWidget.clickAddButton();
        anAssert.isTrue(productWidget.isAllRequiredElementsVisibleAfterClickAddBtn(),
                "После нажатия на кнопку 'Добавить' не отобразились необходимые элементы");
        return this;
    }

    @Step("Ввести {value} кол-во товара для {productIndex}-ого товара")
    public ExtendedSearchModal enterQuantity(int value, int productIndex) throws Exception {
        productIndex--;
        AddProductsFromSearchWidget productWidget = productWidgetList.get(productIndex);
        productWidget.enterQuantity(value);
        return this;
    }

    @Step("Закрыть модальное окно")
    public void closeModalWindow() {
        closeBtn.click();
        closeBtn.waitForInvisibility();
        waitForSpinnerDisappear();
    }

    // Verifications

    @Step("Проверить, что все товары в окне имеют в названии {value}")
    public ExtendedSearchModal shouldProductsContainInTitle(String value, int limitCheck) {
        List<ProductOrderCardWebData> products = productWidgetList.getDataList(limitCheck);
        for (ProductOrderCardWebData product : products) {
            anAssert.isContainsIgnoringCase(product.getTitle(), value,
                    String.format("Товар с ЛМ код %s не содержит в названии '%s'", product.getLmCode(), value));
        }
        anAssert.isTrue(products.size() > 0,
                "Не найден ни один товар, содержащий в названии '" + value + "'");
        return this;
    }

}
