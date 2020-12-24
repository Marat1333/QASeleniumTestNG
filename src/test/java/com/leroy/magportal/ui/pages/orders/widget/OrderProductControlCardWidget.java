package com.leroy.magportal.ui.pages.orders.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.orders.ControlProductCardData;
import com.leroy.magportal.ui.models.orders.ToGiveAwayProductCardData;
import com.leroy.magportal.ui.pages.orders.GiveAwayShipOrderPage;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

public class OrderProductControlCardWidget extends CardWebWidget<ControlProductCardData> {

    public OrderProductControlCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//p[contains(@class, 'ProductCard__body-title')]", metaName = "Название товара")
    Element title;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__quantities')]//div[contains(@class, 'inputCounter') and descendant::label[contains(text(), 'Заказано')]]//input",
            metaName = "Поле 'Заказано'")
    EditBox orderedQuantity;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__quantities')]//div[contains(@class, 'inputCounter') and descendant::label[contains(text(), 'Собрано')]]//input",
            metaName = "Поле 'Собрано'")
    EditBox collectedQuantity;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__quantities')]//div[contains(@class, 'inputCounter') and descendant::label[contains(text(), 'Контроль')]]//input",
            metaName = "Поле 'Контроль'")
    EditBox controlledQuantity;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__quantities')]//div[contains(@class, 'inputCounter') and descendant::label[contains(text(), 'К выдаче')]]//input",
            metaName = "Поле 'К выдаче'")
    EditBox toGiveAwayQuantity;

    /*@WebFindBy(xpath = "",
            metaName = "Кнопка свертывания")
    Button collapseBtn; */

    @WebFindBy(xpath = "//button[contains(@class, 'lmui-Button-mt-gap2')]//span[contains(@class, 'lmui-Icon-size-ic2')]",
            metaName = "Кнопка развертывания")
    Button expandBtn;

    @WebFindBy(xpath = ".//span[contains(@class, 'Price-container')]", metaName = "Цена")
    Element price;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCardFooter__bigSide')]//p[2]", metaName = "Габариты")
    Element dimension;

    // Actions

    @Step("Нажать кнопку 'Кнопка развертывания'")
    public OrderProductControlCardWidget clickExpandBtn() {
        expandBtn.click();
        return this;
    }

    // Get data
    public String getOrderedQuantity() {return orderedQuantity.getText(); }

    public String collectedQuantity() {
        return collectedQuantity.getText();
    }

    public String controlledQuantity() {
        return controlledQuantity.getText();
    }

    public String toGiveAwayQuantity() {
        return toGiveAwayQuantity.getText();
    }

    
   @Override
    public ControlProductCardData collectDataFromPage() {
       ControlProductCardData controlProductCardData = new ControlProductCardData();

       controlProductCardData.setTitle(title.getText());

        return controlProductCardData;
    }
}

