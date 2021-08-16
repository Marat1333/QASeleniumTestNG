package com.leroy.magportal.ui.pages.cart_estimate.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

public class ProductOrderCardPuzWidget extends CardWebWidget<ProductOrderCardWebData> {

    public ProductOrderCardPuzWidget(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    @WebFindBy(xpath = ".//div[span[normalize-space(.)='ЛМ']]/div/span[not(.='')]", metaName = "LM Code")
    Element lmCode;

    @WebFindBy(xpath = ".//div[contains(@class, 'Dropdown__title')]//span", metaName = "Штрихкод")
    Element barCode;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__title')]//*", metaName = "Название продукта")
    Element title;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__header')]/div[contains(@class, 'lmui-View')][4]/span", metaName = "Вес продукта")
    Element weight;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__price')]/span", metaName = "Цена")
    Element price;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__discount-percent')]/span", metaName = "Скидка")
    Element discountPercent;

    @WebFindBy(xpath = ".//span[contains(@class, 'SalesDocProduct__content__discount-price')]", metaName = "Цена без скидки")
    Element totalPriceWithoutDiscount;

    @WebFindBy(xpath = ".//div[contains(@class, 'Estimate-price')]", metaName = "Окончательная цена")
    Element totalPrice;

    @WebFindBy(xpath = ".//span[@id='inputCounterDecrementButton']/div", metaName = "Кнопка уменьшения количества")
    EditBox minusQuantityBtn;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__qty__input-counter')]//input", metaName = "Количество")
    EditBox quantityFld;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__qty')]//span[1]", metaName = "Единица измерения количества")
    Element quantityDeliveryLbl;

    @WebFindBy(xpath = ".//span[@id='inputCounterIncrementButton']/div", metaName = "Кнопка увеличения количества")
    EditBox plusQuantityBtn;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__available')]//span", metaName = "Доступно")
    Element availableStock;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__buttons')]/div[last()]//button", metaName = "Кнопка открытия контекстного меню")
    Element contextMenu;

    @WebFindBy(id = "0_onCopyProductButton", metaName = "Кнопка 'Добавить еще продукт'")
    Element copyBtn;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__buttons')]/div[2]//button", metaName = "Кнопка скидки")
    Element discountBtn;

    @WebFindBy(id = "0_onDeleteProductButton", metaName = "Кнопка удаления продукта")
    Element deleteBtn;

    public String getLmCode() {
        return lmCode.getText();
    }

    public String getBarCode() {
        return ParserUtil.strWithOnlyDigits(barCode.getTextIfPresent());
    }

    public String getTitle() {
        return title.getText();
    }

    public String getWeight() {
        return weight.getText().replaceAll("[\\w|,]", "");
    }

    public String getPrice() {
        return price.isVisible() ? price.getText() : totalPriceWithoutDiscount.isVisible() ? getTotalPriceWithoutDiscount() : getTotalPrice();
    }

    public String getTotalPrice() {
        String result = totalPrice.getTextIfPresent();
        if (result == null)
            result = E(getXpath() + "//div[contains(@class, 'SalesDocProduct__content__price')]/*").getText();
        return result;
    }

    public String getDiscountPercent() {
        return discountPercent.getTextIfPresent();
    }

    public String getTotalPriceWithoutDiscount() {
        return totalPriceWithoutDiscount.getTextIfPresent();
    }

    public String getQuantity() {
        return quantityFld.getText();
    }

    public String getAvailableStock() {
        return availableStock.getText();
    }

    @Override
    public ProductOrderCardWebData collectDataFromPage() {
        ProductOrderCardWebData productOrderCardWebData = new ProductOrderCardWebData();
        productOrderCardWebData.setLmCode(getLmCode());
        productOrderCardWebData.setBarCode(getBarCode());
        productOrderCardWebData.setTitle(getTitle());
        if (productOrderCardWebData.getTitle().toLowerCase().equals("доставка")) {
            productOrderCardWebData.setService(true);
            productOrderCardWebData.setSelectedQuantity(ParserUtil.strToDouble(quantityDeliveryLbl.getText()));
        } else {
            productOrderCardWebData.setWeight(ParserUtil.strToDouble(getWeight()));
            productOrderCardWebData.setSelectedQuantity(ParserUtil.strToDouble(getQuantity()));
            productOrderCardWebData.setAvailableTodayQuantity(ParserUtil.strToDouble(getAvailableStock()));
        }
        productOrderCardWebData.setPrice(ParserUtil.strToDouble(getPrice()));
        if (totalPriceWithoutDiscount.isVisible()) {
            productOrderCardWebData.setTotalPrice(ParserUtil.strToDouble(getTotalPriceWithoutDiscount()));
            productOrderCardWebData.setTotalPriceWithDiscount(ParserUtil.strToDouble(getTotalPrice()));
        } else
            productOrderCardWebData.setTotalPrice(ParserUtil.strToDouble(getTotalPrice()));
        productOrderCardWebData.setDiscountPercent(ParserUtil.strToDouble(getDiscountPercent()));
        return productOrderCardWebData;
    }

    public void editQuantity(Number value) {
        quantityFld.clear(true);
        quantityFld.fill(String.valueOf(value.intValue()));
        quantityFld.sendBlurEvent();
    }

    public void clickPlusQuantity() {
        plusQuantityBtn.click();
    }

    public void clickMinusQuantity() {
        minusQuantityBtn.click();
    }

    public void clickCopy() {
        openContextMenu();
        copyBtn.click();
    }

    public void clickCreateDiscount() {
        discountBtn.click();
    }

    public Color getColorOfAvailableStockLbl() {
        return availableStock.getColor();
    }

    public void openContextMenu(){
        contextMenu.click();
    }

    public void clickDelete() {
        openContextMenu();
        try {
            deleteBtn.click();
        } catch (ElementClickInterceptedException err) {
            Log.error(err.getMessage());
            deleteBtn.scrollTo();
            deleteBtn.click();
        }
    }
}
