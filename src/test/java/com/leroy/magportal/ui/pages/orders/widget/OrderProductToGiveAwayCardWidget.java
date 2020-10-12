package com.leroy.magportal.ui.pages.orders.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.orders.ToGiveAwayProductCardData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import org.openqa.selenium.WebDriver;

public class OrderProductToGiveAwayCardWidget extends CardWebWidget<ToGiveAwayProductCardData> {

    public OrderProductToGiveAwayCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = "//p[contains(@class, 'ProductCard__body-title')]", metaName = "Название товара")
    Element title;

    @WebFindBy(xpath = "//div[contains(@class, 'ProductCard__quantities')]//div[contains(@class, 'inputCounter') and descendant::label[contains(text(), 'К выдаче')]]//input",
            metaName = "Поле 'К выдаче'")
    EditBox toGiveAwayQuantity;

    @WebFindBy(xpath = ".//span[contains(@class, 'Price-container')]", metaName = "Цена")
    Element price;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCardFooter__bigSide')]//p[2]", metaName = "Габариты")
    Element dimension;


    public String getToGiveAwayQuantity() {
        return toGiveAwayQuantity.getText();
    }



    // Actions

    public void editQuantity(int value) {
        toGiveAwayQuantity.clear(true);
        toGiveAwayQuantity.fill(String.valueOf(value));
        toGiveAwayQuantity.sendBlurEvent();
    }

    @Override
    public ToGiveAwayProductCardData collectDataFromPage() {
        ToGiveAwayProductCardData toGiveAwayProductCardData = new ToGiveAwayProductCardData();

        toGiveAwayProductCardData.setTitle(title.getText());

        return toGiveAwayProductCardData;
    }
}

