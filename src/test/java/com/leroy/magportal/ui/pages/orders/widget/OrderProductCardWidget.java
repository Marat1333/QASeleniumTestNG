package com.leroy.magportal.ui.pages.orders.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.pages.orders.GiveAwayShipOrderPage;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

public class OrderProductCardWidget extends CardWebWidget<ProductOrderCardWebData> {

    public OrderProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//div[contains(@class, 'shared-order-StyledLabel')]", metaName = "Номер отдела")
    Element department;

    @WebFindBy(xpath = ".//div[span[contains(@class, 'LmCode__accent')]]", metaName = "ЛМ код")
    Element lmCode;

    @WebFindBy(xpath = ".//div[contains(@class, 'roductCard__header')]//button", metaName = "Корзина (кнопка для удаления)")
    Button trashBtn;

    @WebFindBy(xpath = ".//div[contains(@class, 'Dropdown__title')]//button", metaName = "Бар код")
    Element barCode;

    @WebFindBy(xpath = ".//p[contains(@class, 'ProductCard__body-title')]", metaName = "Название товара")
    Element title;

    @WebFindBy(xpath = ".//div[contains(@class, 'Discount')]//span", metaName = "Скидка %")
    Element discountPercent;

    @WebFindBy(xpath = ".//span[contains(@class, 'Price-container')]", metaName = "Цена")
    Element price;

    @WebFindBy(xpath = ".//div//p[contains(text(), 'Вес')]/following-sibling::p", metaName = "Вес")
    Element weight;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCardFooter__bigSide')]//p[2]", metaName = "Габариты")
    Element dimension;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__quantities')]//span", metaName = "Кол-во 'Доступно'")
    Element availableQuantity;

    public String getAvailableQuantity() {
        return availableQuantity.getText();
    }

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__quantities')]//div[contains(@class, 'inputCounter') and descendant::label[contains(text(), 'Заказано')]]//input",
            metaName = "Поле 'Заказано'")
    EditBox orderedQuantityFld;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__quantities')]//div[contains(@class, 'inputCounter') and descendant::label[contains(text(), 'К доставке')]]//input", metaName = "Поле 'Заказано'")
    EditBox toDeliveryQuantityFld;

    public String getOrderedQuantity() {
        return orderedQuantityFld.getText();
    }

    public String getWeight() {
        return weight.getText();
    }

    // Actions

    public void clickTrashBtn() {
        trashBtn.click();
    }

    public void editQuantity(Double value) {
        orderedQuantityFld.clear(true);
        orderedQuantityFld.fill(String.valueOf(value));
        orderedQuantityFld.sendBlurEvent();
    }

    public void editToDeliveryQuantity(double value) {
        toDeliveryQuantityFld.clear(true);
        toDeliveryQuantityFld.fill(String.valueOf(value));
        toDeliveryQuantityFld.sendBlurEvent();
    }

    @Override
    public ProductOrderCardWebData collectDataFromPage() {
        ProductOrderCardWebData productOrderCardWebData = new ProductOrderCardWebData();
        productOrderCardWebData.setBarCode(ParserUtil.strWithOnlyDigits(barCode.getTextIfPresent()));
        productOrderCardWebData.setLmCode(lmCode.getText());
        productOrderCardWebData.setTitle(title.getText());
        productOrderCardWebData.setAvailableTodayQuantity(ParserUtil.strToDouble(availableQuantity.getText()));
        productOrderCardWebData.setSelectedQuantity(ParserUtil.strToDouble(orderedQuantityFld.getText()));
        productOrderCardWebData.setWeight(ParserUtil.strToDouble(getWeight(), ".") * productOrderCardWebData.getSelectedQuantity());
        if (!discountPercent.isVisible())
            productOrderCardWebData.setPrice(ParserUtil.strToDouble(price.getText()));
        else {
            double priceWithDiscount = ParserUtil.strToDouble(price.getText());
            productOrderCardWebData.setDiscountPercent(ParserUtil.strToDouble(discountPercent.getText()));
            productOrderCardWebData.setTotalPriceWithDiscount(priceWithDiscount * productOrderCardWebData.getSelectedQuantity());
            productOrderCardWebData.setPrice(ParserUtil.plus(priceWithDiscount / (1 - productOrderCardWebData.getDiscountPercent() / 100.0), 0, 2));
        }
        productOrderCardWebData.setTotalPrice(ParserUtil.plus(productOrderCardWebData.getPrice() * productOrderCardWebData.getSelectedQuantity(), 0, 2));
        return productOrderCardWebData;
    }
}

