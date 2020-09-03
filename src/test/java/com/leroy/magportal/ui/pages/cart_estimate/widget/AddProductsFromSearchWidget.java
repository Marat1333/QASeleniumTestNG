package com.leroy.magportal.ui.pages.cart_estimate.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class AddProductsFromSearchWidget extends CardWebWidget<ProductOrderCardWebData> {

    public AddProductsFromSearchWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//span[contains(@class, 'LmCode__accent')]/following-sibling::span", metaName = "ЛМ код")
    Element lmCode;

    @WebFindBy(xpath = ".//div[contains(@class, 'BarViewProductCard__container')]/div/div[2]/span[2]", metaName = "Бар код")
    Element barCode;

    @WebFindBy(xpath = ".//p[contains(@class, 'BarViewProductCard__title')]", metaName = "Название товара")
    Element title;

    @WebFindBy(xpath = ".//p[contains(@class, 'BarViewProductCard__title')]/following-sibling::div//span[1]",
            metaName = "Доступное кол-во")
    Element availableQuantity;

    @WebFindBy(xpath = ".//p[contains(@class, 'BarViewProductCard__title')]/following-sibling::div/div[2]//span",
            metaName = "Цена")
    Element price;

    private final static String FOOTER_XPATH = ".//div[contains(@class, 'BarViewProductCard__footer')]";

    @WebFindBy(xpath = FOOTER_XPATH + "//button[descendant::span[text()='ДОБАВИТЬ']]",
            metaName = "Кнопка Добавить")
    Button addBtn;

    @WebFindBy(xpath = FOOTER_XPATH
            + "//div[input[@id='inputCounterTextInput']]/preceding-sibling::div",
            metaName = "Кнопка '-'")
    Element minusBtn;

    @WebFindBy(xpath = FOOTER_XPATH + "//input[@id='inputCounterTextInput']",
            metaName = "Поле кол-ва")
    EditBox quantityFld;

    @WebFindBy(xpath = FOOTER_XPATH
            + "//div[input[@id='inputCounterTextInput']]/following-sibling::div",
            metaName = "Кнопка '+'")
    Element plusBtn;

    @WebFindBy(xpath = FOOTER_XPATH + "//button[not(descendant::span[text()='ДОБАВИТЬ'])]",
            metaName = "Кнопка удалить (мусорка)")
    Element trashBtn;

    @Override
    public ProductOrderCardWebData collectDataFromPage() {
        ProductOrderCardWebData productData = new ProductOrderCardWebData();
        productData.setTitle(title.getText());
        productData.setPrice(ParserUtil.strToDouble(price.getText()));
        productData.setAvailableTodayQuantity(ParserUtil.strToDouble(availableQuantity.getText()));
        productData.setLmCode(lmCode.getText());
        productData.setBarCode(ParserUtil.strWithOnlyDigits(barCode.getText()));
        if (quantityFld.isVisible()) {
            productData.setSelectedQuantity(ParserUtil.strToDouble(quantityFld.getText()));
        }
        return productData;
    }

    public void clickAddButton() {
        addBtn.click();
        trashBtn.waitForVisibility();
    }

    public void enterQuantity(int value) {
        quantityFld.clear(true);
        quantityFld.fill(String.valueOf(value));
        quantityFld.submit();
    }

    public boolean isAllRequiredElementsVisibleAfterClickAddBtn() {
        return !addBtn.isVisible() && minusBtn.isVisible() && quantityFld.isVisible() && plusBtn
                .isVisible() && trashBtn.isVisible();
    }

}
